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

import ru.olegivo.afs.shared.datetime.ADate
import ru.olegivo.afs.shared.db.AfsDatabase
import ru.olegivo.afs.shared.reserve.db.models.ReservedSchedules
import ru.olegivo.afs.shared.schedules.db.ReserveDao
import ru.olegivo.afs.shared.schedules.db.models.ReservedScheduleEntity
import javax.inject.Inject

class ReserveDaoImpl @Inject constructor(
    db: AfsDatabase
) : ReserveDao {
    private val queries = db.reservedScheduleQueries

    override suspend fun getReservedScheduleIds(from: ADate, until: ADate): List<Long> =
        queries.getReservedScheduleIds(from, until)
            .executeAsList() // TODO: ioDispatcher?

    override suspend fun isScheduleReserved(scheduleId: Long): Boolean =
        queries.isScheduleReserved(scheduleId)
            .executeAsOne() // TODO: ioDispatcher?

    override fun insert(vararg obj: ReservedScheduleEntity) =
        queries.transaction {
            obj.forEach {
                queries.insert(it.toNewEntity())
            }
        }

    override fun upsert(objects: List<ReservedScheduleEntity>) =
        queries.transaction {
            objects.forEach {
                queries.upsert(it.toNewEntity())
            }
        }
}

private fun ReservedScheduleEntity.toNewEntity() =
    ReservedSchedules(id, datetime)
