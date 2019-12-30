package ru.olegivo.afs.favorites.android

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import io.reactivex.Completable
import ru.olegivo.afs.MainActivity
import ru.olegivo.afs.R
import ru.olegivo.afs.favorites.domain.ScheduleReminderNotifier
import ru.olegivo.afs.favorites.domain.models.FavoriteRecordReminderParameters
import ru.olegivo.afs.schedules.domain.models.Schedule
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Named

class ScheduleReminderNotifierImpl @Inject constructor(
    @Named("application") private val context: Context
) :
    ScheduleReminderNotifier {
    override fun showNotification(schedule: Schedule): Completable =
        Completable.fromCallable {
            val pendingIntent = getPendingIntent(context, schedule)

            createNotificationChannel(context)

            val builder = NotificationCompat.Builder(
                context,
                FAVORITE_RECORD_REMINDER_CHANNEL_ID
            )
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

    private fun getPendingIntent(context: Context, schedule: Schedule): PendingIntent {
        // TODO: если заполнены личные данные для записи и принято соглашение, можно сразу записывать и показывать уведомление о результатах записи
        return MainActivity.createIntent(
            context,
            FavoriteRecordReminderParameters(schedule.id, schedule.clubId)
        )
    }

    companion object {
        private const val FAVORITE_RECORD_REMINDER_CHANNEL_ID: String =
            "FAVORITE_RECORD_REMINDER_CHANNEL"

        private const val FORMAT = "HH:mm"

        private val hoursMinutesFormat: SimpleDateFormat by lazy {
            // TODO: copy paste
            SimpleDateFormat(FORMAT, Locale.getDefault())
        }

    }
}