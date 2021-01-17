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

import ru.olegivo.afs.common.db.FakeAfsDatabase
import ru.olegivo.afs.common.db.FakeBaseDao
import ru.olegivo.afs.shared.common.db.BaseDao
import ru.olegivo.afs.shared.datetime.ADate
import ru.olegivo.afs.shared.favorites.db.FavoriteDao
import ru.olegivo.afs.shared.favorites.db.models.FavoriteFilterEntity
import ru.olegivo.afs.shared.favorites.db.models.RecordReminderScheduleEntity

class FakeFavoriteDao(private val tables: FakeAfsDatabase.Tables) :
    FavoriteDao,
    BaseDao<FavoriteFilterEntity> by FakeBaseDao(tables.favoriteFilters, { id }) {

    override suspend fun getFavoriteFilters(): List<FavoriteFilterEntity> =
        tables.favoriteFilters.values.toList()

    override fun removeFilter(
        groupId: Int,
        activityId: Int,
        dayOfWeek: Int,
        minutesOfDay: Int
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun exist(
        groupId: Int,
        activityId: Int,
        dayOfWeek: Int,
        minutesOfDay: Int
    ): Boolean = tables.favoriteFilters.values.any {
        it.groupId == groupId &&
            it.activityId == activityId &&
            it.dayOfWeek == dayOfWeek &&
            it.minutesOfDay == minutesOfDay
    }

    override suspend fun getActiveRecordReminderScheduleIds(moment: ADate): List<Long> {
        TODO("Not yet implemented")
    }

    override fun addReminderToRecord(recordReminder: RecordReminderScheduleEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun hasPlannedReminderToRecord(scheduleId: Long): Boolean {
        TODO("Not yet implemented")
    }
}
