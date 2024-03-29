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

package ru.olegivo.afs.schedules.db

import kotlinx.datetime.Instant
import ru.olegivo.afs.common.db.FakeAfsDatabase
import ru.olegivo.afs.common.db.FakeBaseDao
import ru.olegivo.afs.shared.common.db.BaseDao
import ru.olegivo.afs.shared.reserve.db.models.ReservedSchedules
import ru.olegivo.afs.shared.schedules.db.ReserveDao

class FakeReserveDao(private val tables: FakeAfsDatabase.Tables) :
    ReserveDao,
    BaseDao<ReservedSchedules> by FakeBaseDao(tables.reservedSchedules, { id }) {

    override suspend fun getReservedScheduleIds(from: Instant, until: Instant): List<Long> {
        TODO("Not yet implemented")
    }

    override suspend fun isScheduleReserved(scheduleId: Long): Boolean =
        tables.reservedSchedules.values.any {
            it.id == scheduleId
        }
}
