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

import io.reactivex.Maybe
import io.reactivex.Single
import ru.olegivo.afs.common.db.BaseRxDao
import ru.olegivo.afs.common.db.FakeAfsDatabase
import ru.olegivo.afs.common.db.FakeBaseRxDao
import ru.olegivo.afs.extensions.toSingle
import ru.olegivo.afs.schedules.db.models.ScheduleEntity
import java.util.Date

class FakeScheduleDao(private val tables: FakeAfsDatabase.Tables) :
    ScheduleDao(),
    BaseRxDao<ScheduleEntity> by FakeBaseRxDao(tables.schedules, { id }) {

    override fun getSchedules(clubId: Int, from: Date, until: Date): Maybe<List<ScheduleEntity>> {
        TODO("Not yet implemented")
    }

    override fun getSchedules(ids: List<Long>): Single<List<ScheduleEntity>> {
        TODO("Not yet implemented")
    }

    override fun getSchedule(id: Long) = tables.schedules.values.single {
        it.id == id
    }.toSingle()

    override fun filterSchedules(
        clubId: Int,
        groupId: Int,
        activityId: Int
    ) = tables.schedules.values.filter {
        it.clubId == clubId && it.groupId == groupId && it.activityId == activityId
    }.toSingle()
}
