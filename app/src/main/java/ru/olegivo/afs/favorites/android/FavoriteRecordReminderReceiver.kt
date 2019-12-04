package ru.olegivo.afs.favorites.android

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import ru.olegivo.afs.favorites.domain.models.FavoriteRecordReminderParameters

class FavoriteRecordReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        if (intent.action == ACTION_PLAN) {
            WorkManager.getInstance(context)
                .enqueueUniqueWork(
                    FavoriteRecordReminderWorker.TAG,
                    ExistingWorkPolicy.APPEND,
                    OneTimeWorkRequestBuilder<FavoriteRecordReminderWorker>()
                        .setInputData(FavoriteRecordReminderWorker.createInputData(intent.getExtraFavoriteRecordReminderParameters()))
                        .build()
                )

        }
    }

    companion object {
        private const val ACTION_PLAN = "PLAN_FAVORITE_RECORD_REMINDER"

        fun getAlarmIntent(
            context: Context,
            recordReminderParameters: FavoriteRecordReminderParameters
        ): PendingIntent =
            Intent(context, FavoriteRecordReminderReceiver::class.java)
                .setAction(ACTION_PLAN)
                .putFavoriteRecordReminderParameters(recordReminderParameters)
                .let { intent ->
                    PendingIntent.getBroadcast(context, 0, intent, 0)
                }
    }
}
