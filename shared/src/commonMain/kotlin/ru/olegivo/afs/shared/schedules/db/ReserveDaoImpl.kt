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

package ru.olegivo.afs.shared.schedules.db

import ru.olegivo.afs.shared.datetime.ADate
import ru.olegivo.afs.shared.db.AfsDatabase
import ru.olegivo.afs.shared.reserve.db.models.ReservedSchedules

class ReserveDaoImpl constructor(db: AfsDatabase) : ReserveDao {
    private val queries = db.reservedScheduleQueries

    override suspend fun getReservedScheduleIds(from: ADate, until: ADate): List<Long> =
        queries.getReservedScheduleIds(from, until)
            .executeAsList() // TODO: ioDispatcher?

    override suspend fun isScheduleReserved(scheduleId: Long): Boolean =
        queries.isScheduleReserved(scheduleId)
            .executeAsOne() // TODO: ioDispatcher?

    override fun insert(vararg obj: ReservedSchedules) =
        queries.transaction {
            obj.forEach {
                queries.insert(it)
            }
        }

    override fun upsert(objects: List<ReservedSchedules>) =
        queries.transaction {
            objects.forEach {
                queries.upsert(it)
            }
        }
}
