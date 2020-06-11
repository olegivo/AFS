/*
 * Copyright (C) 2020 Oleg Ivashchenko <olegivo@gmail.com>
 *
 * This file is part of AFS.
 *
 * AFS is free software: you can redistribute it and/or modify
 * it under the terms of the MIT License.
 *
 * AFS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * AFS.
 */

package ru.olegivo.afs.favorites.android

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.android.AndroidInjection
import ru.olegivo.afs.favorites.domain.models.FavoriteRecordReminderParameters
import javax.inject.Inject

class FavoriteRecordReminderReceiver : BroadcastReceiver() {

    @Inject
    lateinit var workManager: WorkManager

    override fun onReceive(context: Context, intent: Intent) {
        AndroidInjection.inject(this, context)

        if (intent.action == ACTION_PLAN) {
            val recordReminderParameters = intent.getExtraFavoriteRecordReminderParameters()
            workManager.enqueueUniqueWork(
                FavoriteRecordReminderWorker.TAG,
                ExistingWorkPolicy.APPEND,
                OneTimeWorkRequestBuilder<FavoriteRecordReminderWorker>()
                    .setInputData(
                        FavoriteRecordReminderWorker.createInputData(
                            recordReminderParameters
                        )
                    )
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
                    PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_ONE_SHOT)
                }
    }
}
