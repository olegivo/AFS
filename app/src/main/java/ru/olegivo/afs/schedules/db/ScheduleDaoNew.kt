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

import com.squareup.sqldelight.runtime.rx.asMaybe
import com.squareup.sqldelight.runtime.rx.asSingle
import com.squareup.sqldelight.runtime.rx.mapToList
import com.squareup.sqldelight.runtime.rx.mapToOne
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.rxkotlin.toCompletable
import io.reactivex.schedulers.Schedulers
import ru.olegivo.afs.common.db.AfsDatabaseNew
import ru.olegivo.afs.extensions.parallelMapList
import ru.olegivo.afs.schedules.db.models.ScheduleEntity
import ru.olegivo.afs.schedules.db.models.Schedules
import java.util.Date
import javax.inject.Inject
import javax.inject.Named

class ScheduleDaoNew @Inject constructor(
    db: AfsDatabaseNew,
    @Named("io") private val ioScheduler: Scheduler
) : ScheduleDao() {
    private val queries = db.scheduleDbQueries

    override fun getSchedules(clubId: Int, from: Date, until: Date): Maybe<List<ScheduleEntity>> =
        queries.getSchedules(
            from = from,
            until = until,
            clubId = clubId
        )
            .asMaybe(ioScheduler)
            .mapToList()
            .parallelMapList(Schedulers.computation()) {
                it.toOldEntity()
            }

    override fun getSchedules(ids: List<Long>): Single<List<ScheduleEntity>> =
        queries.getSchedulesByIds(ids)
            .asSingle(ioScheduler)
            .mapToList()
            .parallelMapList(Schedulers.computation()) {
                it.toOldEntity()
            }

    override fun getSchedule(id: Long): Single<ScheduleEntity> =
        queries.getSchedule(id)
            .asSingle(ioScheduler)
            .mapToOne()
            .map { it.toOldEntity() }

    override fun filterSchedules(
        clubId: Int,
        groupId: Int,
        activityId: Int
    ): Single<List<ScheduleEntity>> =
        queries.filterSchedules(clubId = clubId, groupId = groupId, activityId = activityId)
            .asSingle(ioScheduler)
            .mapToList()
            .parallelMapList(Schedulers.computation()) {
                it.toOldEntity()
            }

    override fun insert(vararg obj: ScheduleEntity): Completable =
        {
            queries.transaction {
                obj.forEach {
                    queries.insert(it.toNewEntity())
                }
            }
        }
            .toCompletable()
            .subscribeOn(ioScheduler)

    override fun upsert(objects: List<ScheduleEntity>): Completable =
        {
            queries.transaction {
                objects.forEach {
                    queries.upsert(it.toNewEntity())
                }
            }
        }
            .toCompletable()
            .subscribeOn(ioScheduler)
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
