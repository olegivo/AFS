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

package ru.olegivo.afs.shared.favorites.db

import ru.olegivo.afs.shared.datetime.ADate
import ru.olegivo.afs.shared.db.AfsDatabase
import ru.olegivo.afs.shared.favorites.db.models.FavoriteFilterEntity
import ru.olegivo.afs.shared.favorites.db.models.FavoriteFilters
import ru.olegivo.afs.shared.favorites.db.models.RecordReminderScheduleEntity
import ru.olegivo.afs.shared.recordReminders.db.models.RecordReminderSchedules

class FavoriteDaoImpl constructor(db: AfsDatabase) : FavoriteDao {
    private val favoriteFilterQueries = db.favoriteFilterQueries
    private val recordReminderScheduleQueries = db.recordReminderScheduleQueries

    override suspend fun getFavoriteFilters(): List<FavoriteFilterEntity> =
        favoriteFilterQueries.getFavoriteFilters()
            .executeAsList()
            .map { it.toOldEntity() } // TODO: ioDispatcher?

    override fun removeFilter(
        groupId: Int,
        activityId: Int,
        dayOfWeek: Int,
        minutesOfDay: Int
    ) = favoriteFilterQueries.removeFilter(
        groupId = groupId,
        activityId = activityId,
        dayOfWeek = dayOfWeek,
        minutesOfDay = minutesOfDay
    )

    override suspend fun exist(
        groupId: Int,
        activityId: Int,
        dayOfWeek: Int,
        minutesOfDay: Int
    ): Boolean =
        favoriteFilterQueries.exist(
            groupId = groupId,
            activityId = activityId,
            dayOfWeek = dayOfWeek,
            minutesOfDay = minutesOfDay
        )
            .executeAsOne()// TODO: ioDispatcher?

    override suspend fun getActiveRecordReminderScheduleIds(moment: ADate): List<Long> =
        recordReminderScheduleQueries.getActiveRecordReminderScheduleIds(moment)
            .executeAsList() // TODO: ioDispatcher?

    override fun addReminderToRecord(recordReminder: RecordReminderScheduleEntity) {
        recordReminderScheduleQueries.addReminderToRecord(recordReminder.toNewEntity())
    }

    override suspend fun hasPlannedReminderToRecord(scheduleId: Long): Boolean =
        recordReminderScheduleQueries.hasPlannedReminderToRecord(scheduleId)
            .executeAsOne() // TODO: ioDispatcher?

    override fun insert(vararg obj: FavoriteFilterEntity) =
        favoriteFilterQueries.transaction {
            obj.forEach {
                favoriteFilterQueries.insert(it.toNewEntity())
            }
        }

    override fun upsert(objects: List<FavoriteFilterEntity>) =
        favoriteFilterQueries.transaction {
            objects.forEach {
                favoriteFilterQueries.upsert(it.toNewEntity())
            }
        }
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
