/*
 * Copyright (C) 2021 Oleg Ivashchenko <olegivo@gmail.com>
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

import com.squareup.sqldelight.runtime.rx.asSingle
import com.squareup.sqldelight.runtime.rx.mapToList
import com.squareup.sqldelight.runtime.rx.mapToOne
import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.rxkotlin.toCompletable
import io.reactivex.schedulers.Schedulers
import ru.olegivo.afs.common.db.AfsDatabaseNew
import ru.olegivo.afs.extensions.parallelMapList
import ru.olegivo.afs.favorites.db.models.FavoriteFilters
import ru.olegivo.afs.favorites.db.models.RecordReminderScheduleEntity
import ru.olegivo.afs.recordReminders.db.models.RecordReminderSchedules
import ru.olegivo.afs.shared.favorites.db.models.FavoriteFilterEntity
import java.util.Date
import javax.inject.Inject
import javax.inject.Named

class FavoriteDaoNew @Inject constructor(
    db: AfsDatabaseNew,
    @Named("io") private val ioScheduler: Scheduler
) : FavoriteDao {
    private val favoriteFilterQueries = db.favoriteFilterQueries
    private val recordReminderScheduleQueries = db.recordReminderScheduleQueries

    override fun getFavoriteFilters(): Single<List<FavoriteFilterEntity>> =
        favoriteFilterQueries.getFavoriteFilters()
            .asSingle(ioScheduler)
            .mapToList()
            .parallelMapList(Schedulers.computation()) {
                it.toOldEntity()
            }

    override fun removeFilter(
        groupId: Int,
        activityId: Int,
        dayOfWeek: Int,
        minutesOfDay: Int
    ): Completable {
        return {
            favoriteFilterQueries.removeFilter(
                groupId = groupId,
                activityId = activityId,
                dayOfWeek = dayOfWeek,
                minutesOfDay = minutesOfDay
            )
        }
            .toCompletable()
            .subscribeOn(ioScheduler)
    }

    override fun exist(
        groupId: Int,
        activityId: Int,
        dayOfWeek: Int,
        minutesOfDay: Int
    ): Single<Boolean> =
        favoriteFilterQueries.exist(
            groupId = groupId,
            activityId = activityId,
            dayOfWeek = dayOfWeek,
            minutesOfDay = minutesOfDay
        )
            .asSingle(ioScheduler)
            .mapToOne()

    override fun getActiveRecordReminderScheduleIds(moment: Date): Single<List<Long>> =
        recordReminderScheduleQueries.getActiveRecordReminderScheduleIds(moment)
            .asSingle(ioScheduler)
            .mapToList()

    override fun addReminderToRecord(recordReminder: RecordReminderScheduleEntity): Completable {
        return { recordReminderScheduleQueries.addReminderToRecord(recordReminder.toNewEntity()) }
            .toCompletable()
            .subscribeOn(ioScheduler)
    }

    override fun hasPlannedReminderToRecord(scheduleId: Long): Single<Boolean> =
        recordReminderScheduleQueries.hasPlannedReminderToRecord(scheduleId)
            .asSingle(ioScheduler)
            .mapToOne()

    override fun insertCompletable(vararg obj: FavoriteFilterEntity): Completable =
        {
            favoriteFilterQueries.transaction {
                obj.forEach {
                    favoriteFilterQueries.insert(it.toNewEntity())
                }
            }
        }
            .toCompletable()
            .subscribeOn(ioScheduler)

    override fun upsertCompletable(objects: List<FavoriteFilterEntity>): Completable =
        {
            favoriteFilterQueries.transaction {
                objects.forEach {
                    favoriteFilterQueries.upsert(it.toNewEntity())
                }
            }
        }
            .toCompletable()
            .subscribeOn(ioScheduler)
}

private fun FavoriteFilterEntity.toNewEntity() =
    FavoriteFilters(
        id = id,
        clubId = clubId,
        groupId = groupId,
        group = group,
        activityId = activityId,
        activity = activity,
        dayOfWeek = dayOfWeek,
        minutesOfDay = minutesOfDay
    )

private fun FavoriteFilters.toOldEntity() =
    FavoriteFilterEntity(
        id = id,
        clubId = clubId,
        groupId = groupId,
        group = group,
        activityId = activityId,
        activity = activity,
        dayOfWeek = dayOfWeek,
        minutesOfDay = minutesOfDay
    )

private fun RecordReminderScheduleEntity.toNewEntity() =
    RecordReminderSchedules(
        scheduleId = scheduleId,
        dateFrom = dateFrom,
        dateUntil = dateUntil
    )

private fun RecordReminderSchedules.toOldEntity() =
    RecordReminderScheduleEntity(
        scheduleId = scheduleId,
        dateFrom = dateFrom,
        dateUntil = dateUntil
    )
