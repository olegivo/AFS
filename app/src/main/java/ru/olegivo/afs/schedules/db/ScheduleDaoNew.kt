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

import io.reactivex.Scheduler
import ru.olegivo.afs.common.db.AfsDatabaseNew
import ru.olegivo.afs.schedules.db.models.Schedules
import ru.olegivo.afs.shared.datetime.ADate
import ru.olegivo.afs.shared.schedules.db.models.ScheduleEntity
import javax.inject.Inject
import javax.inject.Named

class ScheduleDaoNew @Inject constructor(
    db: AfsDatabaseNew,
    @Named("io") private val ioScheduler: Scheduler
) : ScheduleDao {
    private val queries = db.scheduleDbQueries

    override suspend fun getSchedules(
        clubId: Int,
        from: ADate,
        until: ADate
    ): List<ScheduleEntity>? =
        queries.getSchedules(
            from = from,
            until = until,
            clubId = clubId
        )
            .executeAsList() // TODO: ioDispatcher?
            .map { it.toOldEntity() }
            .takeIf { it.isNotEmpty() } // TODO: ioDispatcher?

    override suspend fun getSchedules(ids: List<Long>): List<ScheduleEntity> =
        queries.getSchedulesByIds(ids)
            .executeAsList() // TODO: ioDispatcher?
            .map { it.toOldEntity() }

    override suspend fun getSchedule(id: Long): ScheduleEntity =
        queries.getSchedule(id)
            .executeAsOne() // TODO: ioDispatcher?
            .toOldEntity()

    override suspend fun filterSchedules(
        clubId: Int,
        groupId: Int,
        activityId: Int
    ): List<ScheduleEntity> =
        queries.filterSchedules(clubId = clubId, groupId = groupId, activityId = activityId)
            .executeAsList() // TODO: ioDispatcher?
            .map { it.toOldEntity() }

    override fun insert(vararg obj: ScheduleEntity) =
        queries.transaction {
            obj.forEach {
                queries.insert(it.toNewEntity())
            }
        }

    override fun upsert(objects: List<ScheduleEntity>) =
        queries.transaction {
            objects.forEach {
                queries.upsert(it.toNewEntity())
            }
        }
}

fun Schedules.toOldEntity() =
    ScheduleEntity(
        id = id,
        clubId = clubId,
        groupId = groupId,
        group = group,
        activityId = activityId,
        activity = activity,
        datetime = datetime,
        length = length,
        preEntry = preEntry,
        totalSlots = totalSlots,
        recordFrom = recordFrom,
        recordTo = recordTo
    )

private fun ScheduleEntity.toNewEntity() =
    Schedules(
        id = id,
        clubId = clubId,
        groupId = groupId,
        group = group,
        activityId = activityId,
        activity = activity,
        datetime = datetime,
        length = length,
        preEntry = preEntry,
        totalSlots = totalSlots,
        recordFrom = recordFrom,
        recordTo = recordTo
    )
