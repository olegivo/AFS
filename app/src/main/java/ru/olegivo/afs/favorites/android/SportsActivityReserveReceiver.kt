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
import javax.inject.Inject

class SportsActivityReserveReceiver : BroadcastReceiver() {

    @Inject
    lateinit var workManager: WorkManager

    override fun onReceive(context: Context, intent: Intent) {
        AndroidInjection.inject(this, context)

        if (intent.action == ACTION_RESERVE) {

            workManager.enqueueUniqueWork(
                SportsActivityReserveWorker.TAG,
                ExistingWorkPolicy.APPEND,
                OneTimeWorkRequestBuilder<SportsActivityReserveWorker>()
                    .setInputData(SportsActivityReserveWorker.createInputData(intent.getSportsActivityReserveParameters()))
                    .build()
            )
        }
    }

    companion object {
        private const val ACTION_RESERVE = "RESERVE_FAVORITE"

        fun createIntent(
            context: Context,
            clubId: Int,
            scheduleId: Long,
            fio: String,
            phone: String
        ): PendingIntent =
            Intent(context, SportsActivityReserveReceiver::class.java)
                .setAction(ACTION_RESERVE)
                .putSportsActivityReserveParameters(
                    SportsActivityReserveParameters(
                        clubId,
                        scheduleId,
                        fio,
                        phone
                    )
                )
                .let { intent ->
                    PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                }
    }
}
