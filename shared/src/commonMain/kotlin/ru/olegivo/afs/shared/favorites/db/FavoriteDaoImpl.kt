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

import kotlinx.datetime.Instant
import ru.olegivo.afs.shared.db.AfsDatabase
import ru.olegivo.afs.shared.favorites.db.models.FavoriteFilters
import ru.olegivo.afs.shared.recordReminders.db.models.RecordReminderSchedules

class FavoriteDaoImpl constructor(db: AfsDatabase) : FavoriteDao {
    private val favoriteFilterQueries = db.favoriteFilterQueries
    private val recordReminderScheduleQueries = db.recordReminderScheduleQueries

    override suspend fun getFavoriteFilters(): List<FavoriteFilters> =
        favoriteFilterQueries.getFavoriteFilters()
            .executeAsList() // TODO: ioDispatcher?

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

    override suspend fun getActiveRecordReminderScheduleIds(moment: Instant): List<Long> =
        recordReminderScheduleQueries.getActiveRecordReminderScheduleIds(moment)
            .executeAsList() // TODO: ioDispatcher?

    override fun addReminderToRecord(recordReminder: RecordReminderSchedules) {
        recordReminderScheduleQueries.addReminderToRecord(recordReminder)
    }

    override suspend fun hasPlannedReminderToRecord(scheduleId: Long): Boolean =
        recordReminderScheduleQueries.hasPlannedReminderToRecord(scheduleId)
            .executeAsOne() // TODO: ioDispatcher?

    override fun insert(vararg obj: FavoriteFilters) =
        favoriteFilterQueries.transaction {
            obj.forEach {
                favoriteFilterQueries.insert(it)
            }
        }

    override fun upsert(objects: List<FavoriteFilters>) =
        favoriteFilterQueries.transaction {
            objects.forEach {
                favoriteFilterQueries.upsert(it)
            }
        }
}
