package ru.olegivo.afs.favorites.android

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

class FavoritesRebootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        WorkManager.getInstance(context)
            .enqueueUniqueWork(
                FavoriteRecordReminderWorker.TAG,
                ExistingWorkPolicy.KEEP,
                OneTimeWorkRequestBuilder<FavoriteRecordReminderWorker>()
                    .setInputData(FavoriteRecordReminderWorker.createInputDataWithAfterRebootMode())
                    .build()
            )
    }
}