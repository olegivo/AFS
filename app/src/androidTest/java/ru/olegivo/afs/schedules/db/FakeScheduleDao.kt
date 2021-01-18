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

package ru.olegivo.afs.schedules.db

import ru.olegivo.afs.common.db.FakeAfsDatabase
import ru.olegivo.afs.common.db.FakeBaseDao
import ru.olegivo.afs.shared.common.db.BaseDao
import ru.olegivo.afs.shared.datetime.ADate
import ru.olegivo.afs.shared.schedules.db.models.ScheduleEntity

class FakeScheduleDao(private val tables: FakeAfsDatabase.Tables) :
    ScheduleDao,
    BaseDao<ScheduleEntity> by FakeBaseDao(tables.schedules, { id }) {

    override suspend fun getSchedules(
        clubId: Int,
        from: ADate,
        until: ADate
    ): List<ScheduleEntity>? {
        TODO("Not yet implemented")
    }

    override suspend fun getSchedules(ids: List<Long>): List<ScheduleEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun getSchedule(id: Long): ScheduleEntity = tables.schedules.values.single {
        it.id == id
    }

    override suspend fun filterSchedules(
        clubId: Int,
        groupId: Int,
        activityId: Int
    ): List<ScheduleEntity> = tables.schedules.values.filter {
        it.clubId == clubId && it.groupId == groupId && it.activityId == activityId
    }
}
