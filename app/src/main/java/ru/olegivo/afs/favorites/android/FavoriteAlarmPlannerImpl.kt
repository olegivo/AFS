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

import android.app.AlarmManager
import android.content.Context
import android.os.Build
import io.reactivex.Completable
import io.reactivex.Scheduler
import ru.olegivo.afs.common.toCalendar
import ru.olegivo.afs.favorites.domain.FavoriteAlarmPlanner
import ru.olegivo.afs.favorites.domain.models.FavoriteRecordReminderParameters
import ru.olegivo.afs.schedules.domain.models.Schedule
import javax.inject.Inject
import javax.inject.Named

class FavoriteAlarmPlannerImpl @Inject constructor(
    @Named("application") private val context: Context,
    @Named("io") private val ioScheduler: Scheduler
) : FavoriteAlarmPlanner {
    override fun planFavoriteRecordReminder(schedule: Schedule): Completable =
        Completable.fromCallable {
            val alarmIntent = FavoriteRecordReminderReceiver.getAlarmIntent(
                context,
                FavoriteRecordReminderParameters(
                    scheduleId = schedule.id,
                    clubId = schedule.clubId
                )
            )

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.apply {
                val recordFrom = schedule.recordFrom!!
                val triggerAtMillis = recordFrom.toCalendar().timeInMillis
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerAtMillis,
                        alarmIntent
                    )
                } else {
                    set(
                        AlarmManager.RTC_WAKEUP,
                        triggerAtMillis,
                        alarmIntent
                    )
                }
            }
        }.subscribeOn(ioScheduler)
}
