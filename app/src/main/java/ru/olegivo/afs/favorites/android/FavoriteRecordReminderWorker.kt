package ru.olegivo.afs.favorites.android

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Data
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import io.reactivex.Completable
import io.reactivex.Single
import ru.olegivo.afs.MainActivity
import ru.olegivo.afs.R
import ru.olegivo.afs.common.android.worker.ChildWorkerFactory
import ru.olegivo.afs.common.domain.DateProvider
import ru.olegivo.afs.extensions.toSingle
import ru.olegivo.afs.favorites.domain.FavoritesRepository
import ru.olegivo.afs.favorites.domain.models.FavoriteRecordReminderParameters
import ru.olegivo.afs.schedules.domain.ScheduleRepository
import ru.olegivo.afs.schedules.domain.models.Schedule
import java.text.SimpleDateFormat
import java.util.*

class FavoriteRecordReminderWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted private val params: WorkerParameters,
    private val scheduleRepository: ScheduleRepository, // TODO: use case
    private val favoritesRepository: FavoritesRepository, // TODO: use case
    private val dateProvider: DateProvider
) : RxWorker(appContext, params) {

    @AssistedInject.Factory
    interface Factory : ChildWorkerFactory

    override fun createWork(): Single<Result> =
        params.inputData.toSingle()
            .flatMapCompletable { process(it) }
            .andThen(Single.just(Result.success()))

    private fun process(data: Data): Completable =
        if (data.isAfterRebootMode()) {
            favoritesRepository.getActiveRecordReminderSchedules(dateProvider.getDate())
                .flatMap {
                    scheduleRepository.getSchedules(it)
                }
                .flatMapCompletable { schedules ->

                    Completable.concat(
                        schedules.map {
                            showNotification(appContext, it)
                                .doOnError { throwable ->
                                    throwable.printStackTrace() /*TODO: log error*/
                                }
                                .onErrorComplete()
                        }
                    )
                }
        } else {
            val scheduleId = data.getScheduleId()
            scheduleRepository.getSchedule(scheduleId)
                .flatMapCompletable { schedule ->
                    showNotification(appContext, schedule)
                }
        }

    private fun getPendingIntent(context: Context, schedule: Schedule): PendingIntent {
        // TODO: если заполнены личные данные для записи и принято соглашение, можно сразу записывать и показывать уведомление о результатах записи
        return MainActivity.createIntent(
            context,
            FavoriteRecordReminderParameters(schedule.id, schedule.clubId)
        )
    }

    private fun showNotification(context: Context, schedule: Schedule): Completable =
        Completable.fromCallable {
            val pendingIntent = getPendingIntent(appContext, schedule)

            createNotificationChannel(context)

            val builder = NotificationCompat.Builder(context, FAVORITE_RECORD_REMINDER_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_check_box_black_24dp)
                .setSubText("Запись на занятие")
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(
                            StringBuilder()
                                .appendln("Занятие: ${schedule.activity}")
                                .appendln("Группа: ${schedule.group}")
                                .appendln("Начало: ${hoursMinutesFormat.format(schedule.datetime)}")
                        )
                        .setBigContentTitle("Записаться на занятие?")
                )
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .addAction(R.drawable.ic_event_black_24dp, "Записаться", pendingIntent)
                //.addAction(R.drawable.ic_cancel_black_24dp, "Отмена", pendingIntent) TODO: отменить напоминание (чтобы после перезагрузки напоминание об этом занятии не всплыло снова)
                .setContentIntent(pendingIntent)
                .setAutoCancel(false)

            with(NotificationManagerCompat.from(context)) {
                // notificationId is a unique int for each notification that you must define
                notify(
                    1,
                    builder.build()
                )//TODO уникальный айдишник уведомления (для данного занятия может быть несколько разных уведомлений, нужно иметь возможность отменить каждое отдельно)
            }
        }

    private fun createNotificationChannel(context: Context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = context.getString(R.string.favorite_record_reminder_channel_name)
            val channelDescription =
                context.getString(R.string.favorite_record_reminder_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val channel = NotificationChannel(
                FAVORITE_RECORD_REMINDER_CHANNEL_ID,
                channelName,
                importance
            ).apply {
                description = channelDescription
            }
            // Register the channel with the system
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val FAVORITE_RECORD_REMINDER_CHANNEL_ID: String =
            "FAVORITE_RECORD_REMINDER_CHANNEL"

        const val TAG = "FavoriteRecordReminderWorker"

        private const val FORMAT = "HH:mm"

        private val hoursMinutesFormat: SimpleDateFormat by lazy {
            // TODO: copy paste
            SimpleDateFormat(FORMAT, Locale.getDefault())
        }

        fun createInputData(recordReminderParameters: FavoriteRecordReminderParameters) =
            workDataOf("SCHEDULE_ID" to recordReminderParameters.scheduleId)

        fun createInputDataWithAfterRebootMode() = workDataOf("AFTER_REBOOT_MODE" to true)

        fun Data.getScheduleId() = getLong("SCHEDULE_ID", 0)
        fun Data.isAfterRebootMode() = getBoolean("AFTER_REBOOT_MODE", false)
    }
}