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

    override fun showNotificationToShowDetails(schedule: Schedule): Completable =
        showNotification(
            notificationTitle = "Записаться на занятие?",
            notificationText = {
                schedule.description {
                    common()
                }
            },
            notificationBuilderAction = {
                val reservePendingIntent = getSportsActivityDetailsPendingIntent(context, schedule)
                setContentIntent(reservePendingIntent)
                addAction(R.drawable.ic_event_black_24dp, "Записаться", reservePendingIntent)
            },
            notificationId = 1
        )

    override fun showNotificationToReserve(schedule: Schedule, fio: String, phone: String) =
        showNotification(
            notificationTitle = "Записаться на занятие?",
            notificationText = {
                schedule.description {
                    common()
                    appendln("ФИО: $fio")
                    appendln("Телефон: $phone")
                }
            },
            notificationBuilderAction = {
                val reservePendingIntent = getReservePendingIntent(context, schedule, fio, phone)
                val sportsActivityDetailsPendingIntent =
                    getSportsActivityDetailsPendingIntent(context, schedule)

                setContentIntent(reservePendingIntent)
                addAction(R.drawable.ic_event_black_24dp, "Записаться", reservePendingIntent)
                addAction(
                    R.drawable.ic_edit_black_24dp,
                    "Записаться с другими контактныеми данными",
                    sportsActivityDetailsPendingIntent
                )
            },
            notificationId = 1
        )

    override fun showAlreadyReserved(schedule: Schedule): Completable = showNotification(
        notificationTitle = "Вы уже записывались на занятие",
        notificationText = {
            schedule.description {
                appendln("Вы уже записывались на это занятие:")
                common()
            }
        },
        notificationBuilderAction = {},
        notificationId = 2
    )

    override fun showHasNoSlotsAPosteriori(schedule: Schedule): Completable = showNotification(
        notificationTitle = "Вы не записаны на занятие",
        notificationText = {
            schedule.description {
                appendln("Не осталось свободных мест. Места закончились до того, как вы отправили запрос.")
                common()
            }
        },
        notificationBuilderAction = {},
        notificationId = 2
    )

    override fun showHasNoSlotsAPriori(schedule: Schedule): Completable = showNotification(
        notificationTitle = "Вы не записаны на занятие",
        notificationText = {
            schedule.description {
                appendln("\"Не осталось свободных мест")
                common()
            }
        },
        notificationBuilderAction = {},
        notificationId = 2
    )

    override fun showTheTimeHasGone(schedule: Schedule): Completable = showNotification(
        notificationTitle = "Вы не записаны на занятие",
        notificationText = {
            schedule.description {
                appendln("Вы не можете записаться на занятие, т.к. время начала уже прошло")
                common()
            }
        },
        notificationBuilderAction = {},
        notificationId = 2
    )

    override fun showSuccessReserved(schedule: Schedule): Completable = showNotification(
        notificationTitle = "Вы записаны на занятие",
        notificationText = {
            schedule.description {
                appendln("")
                common()
            }
        },
        notificationBuilderAction = {},
        notificationId = 2
    )

    private fun Schedule.description(block: ScheduleDescriptor.() -> Unit): StringBuilder =
        ScheduleDescriptor(this)
            .apply(block)
            .getDescription()

    private class ScheduleDescriptor(private val schedule: Schedule) {
        private val stringBuilder = StringBuilder()

        fun common() {
            activity()
            group()
            startTime()
            date()
        }

        fun activity() = appendln("Занятие: ${schedule.activity}")
        fun group() = appendln("Группа: ${schedule.group}")
        fun startTime() = appendln("Начало: ${timeFormat.format(schedule.datetime)}")
        fun date() = appendln("Дата: ${timeFormat.format(schedule.datetime)}")

        fun appendln(string: String) = stringBuilder.appendln(string)


        fun getDescription(): StringBuilder = stringBuilder
    }

    private fun showNotification(
        notificationTitle: String,
        notificationText: () -> CharSequence,
        notificationBuilderAction: NotificationCompat.Builder.() -> Unit,
        notificationId: Int
    ): Completable {
        return Completable.fromCallable {
            createNotificationChannel(context)

            val builder = NotificationCompat.Builder(
                context,
                FAVORITE_RECORD_REMINDER_CHANNEL_ID
            )
                .setSmallIcon(R.drawable.ic_check_box_black_24dp)
                .setSubText("Запись на занятие")
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(notificationText())
                        .setBigContentTitle(notificationTitle)
                )
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                //.addAction(R.drawable.ic_cancel_black_24dp, "Отмена", pendingIntent) TODO: отменить напоминание (чтобы после перезагрузки напоминание об этом занятии не всплыло снова)
                .setAutoCancel(false)
                .apply(notificationBuilderAction)

            with(NotificationManagerCompat.from(context)) {
                // notificationId is a unique int for each notification that you must define
                notify(
                    notificationId,
                    builder.build()
                )//TODO уникальный айдишник уведомления (для данного занятия может быть несколько разных уведомлений, нужно иметь возможность отменить каждое отдельно)
            }
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

    private fun getSportsActivityDetailsPendingIntent(
        context: Context,
        schedule: Schedule
    ): PendingIntent {
        return MainActivity.createIntent(
            context,
            FavoriteRecordReminderParameters(schedule.id, schedule.clubId)
        )
    }

    private fun getReservePendingIntent(
        context: Context,
        schedule: Schedule,
        fio: String,
        phone: String
    ): PendingIntent {
        return SportsActivityReserveReceiver.createIntent(
            context,
            schedule.clubId,
            schedule.id,
            fio,
            phone
        )
    }

    companion object {
        private const val FAVORITE_RECORD_REMINDER_CHANNEL_ID: String =
            "FAVORITE_RECORD_REMINDER_CHANNEL"

        private const val FORMAT_TIME = "HH:mm"
        private const val FORMAT_DATE = "dd.MM.yyyy"

        private val locale = Locale.getDefault()

        private val timeFormat: SimpleDateFormat by lazy {
            // TODO: copy paste
            SimpleDateFormat(FORMAT_TIME, locale)
        }

        private val dateFormat: SimpleDateFormat by lazy {
            // TODO: copy paste
            SimpleDateFormat(FORMAT_DATE, locale)
        }

    }
}
