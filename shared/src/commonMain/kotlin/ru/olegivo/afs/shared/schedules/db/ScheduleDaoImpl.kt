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

import kotlinx.datetime.Instant
import ru.olegivo.afs.shared.db.AfsDatabase
import ru.olegivo.afs.shared.schedules.db.models.Schedules

class ScheduleDaoImpl constructor(db: AfsDatabase) : ScheduleDao {
    private val queries = db.scheduleDbQueries

    override suspend fun getSchedules(
        clubId: Int,
        from: Instant,
        until: Instant
    ): List<Schedules>? =
        queries.getSchedules(
            from = from,
            until = until,
            clubId = clubId
        )
            .executeAsList() // TODO: ioDispatcher?
            .map { it }
            .takeIf { it.isNotEmpty() } // TODO: ioDispatcher?

    override suspend fun getSchedules(ids: List<Long>): List<Schedules> =
        queries.getSchedulesByIds(ids)
            .executeAsList() // TODO: ioDispatcher?
            .map { it }

    override suspend fun getSchedule(id: Long): Schedules =
        queries.getSchedule(id)
            .executeAsOne() // TODO: ioDispatcher?

    override suspend fun filterSchedules(
        clubId: Int,
        groupId: Int,
        activityId: Int
    ): List<Schedules> =
        queries.filterSchedules(clubId = clubId, groupId = groupId, activityId = activityId)
            .executeAsList() // TODO: ioDispatcher?

    override fun insert(vararg obj: Schedules) =
        queries.transaction {
            obj.forEach {
                queries.insert(it)
            }
        }

    override fun upsert(objects: List<Schedules>) =
        queries.transaction {
            objects.forEach {
                queries.upsert(it)
            }
        }
}
