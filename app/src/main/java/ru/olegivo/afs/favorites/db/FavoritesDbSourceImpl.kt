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

package ru.olegivo.afs.favorites.db

import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.rxkotlin.toCompletable
import ru.olegivo.afs.common.CoroutineToRxAdapter
import ru.olegivo.afs.common.toADate
import ru.olegivo.afs.extensions.mapList
import ru.olegivo.afs.favorites.data.FavoritesDbSource
import ru.olegivo.afs.favorites.db.models.toDb
import ru.olegivo.afs.favorites.db.models.toDomain
import ru.olegivo.afs.favorites.domain.models.FavoriteFilter
import ru.olegivo.afs.schedules.domain.models.Schedule
import ru.olegivo.afs.shared.favorites.db.FavoriteDao
import ru.olegivo.afs.shared.recordReminders.db.models.RecordReminderSchedules
import java.util.Date
import javax.inject.Inject
import javax.inject.Named

class FavoritesDbSourceImpl @Inject constructor(
    private val favoriteDao: FavoriteDao,
    @Named("io") private val ioScheduler: Scheduler,
    @Named("computation") private val computationScheduler: Scheduler,
    private val coroutineToRxAdapter: CoroutineToRxAdapter
) : FavoritesDbSource {

    override fun addFilter(favoriteFilter: FavoriteFilter): Completable =
        Single.fromCallable { favoriteFilter.toDb() }
            .subscribeOn(computationScheduler)
            .observeOn(ioScheduler)
            .flatMapCompletable {
                { favoriteDao.insert(it) }
                    .toCompletable()
                    .subscribeOn(ioScheduler)
            }

    override fun removeFilter(favoriteFilter: FavoriteFilter): Completable =
        {
            with(favoriteFilter) {
                favoriteDao.removeFilter(
                    groupId = groupId,
                    activityId = activityId,
                    dayOfWeek = dayOfWeek,
                    minutesOfDay = minutesOfDay
                )
            }
        }
            .toCompletable()
            .subscribeOn(ioScheduler)

    override fun exist(favoriteFilter: FavoriteFilter): Single<Boolean> =
        coroutineToRxAdapter.runToSingle {
            with(favoriteFilter) { favoriteDao.exist(groupId, activityId, dayOfWeek, minutesOfDay) }
        }
            .subscribeOn(ioScheduler)

    override fun getActiveRecordReminderSchedules(moment: Date): Single<List<Long>> =
        coroutineToRxAdapter.runToSingle {
            favoriteDao.getActiveRecordReminderScheduleIds(moment.toADate())
        }
            .subscribeOn(ioScheduler)

    override fun hasPlannedReminderToRecord(schedule: Schedule): Single<Boolean> =
        coroutineToRxAdapter.runToSingle {
            favoriteDao.hasPlannedReminderToRecord(schedule.id)
        }
            .subscribeOn(ioScheduler)

    override fun addReminderToRecord(
        scheduleId: Long,
        dateFrom: Date,
        dateUntil: Date
    ): Completable =
        {
            favoriteDao.addReminderToRecord(
                RecordReminderSchedules(
                    scheduleId = scheduleId,
                    dateFrom = dateFrom.toADate(),
                    dateUntil = dateUntil.toADate()
                )
            )
        }
            .toCompletable()
            .subscribeOn(ioScheduler)

    override fun getFavoriteFilters(): Single<List<FavoriteFilter>> =
        coroutineToRxAdapter.runToSingle { favoriteDao.getFavoriteFilters() }
            .subscribeOn(ioScheduler)
            .observeOn(computationScheduler)
            .mapList { it.toDomain() }
}
