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

import com.squareup.sqldelight.runtime.rx.asSingle
import com.squareup.sqldelight.runtime.rx.mapToList
import com.squareup.sqldelight.runtime.rx.mapToOne
import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.rxkotlin.toCompletable
import ru.olegivo.afs.common.db.AfsDatabaseNew
import ru.olegivo.afs.reserve.db.models.ReservedSchedules
import ru.olegivo.afs.schedules.db.models.ReservedSchedule
import java.util.Date
import javax.inject.Inject
import javax.inject.Named

class ReserveDaoNew @Inject constructor(
    db: AfsDatabaseNew,
    @Named("io") private val ioScheduler: Scheduler
) : ReserveDao {
    private val queries = db.reservedScheduleQueries

    override fun getReservedScheduleIds(from: Date, until: Date): Single<List<Long>> =
        queries.getReservedScheduleIds(from, until)
            .asSingle(ioScheduler)
            .mapToList()

    override fun isScheduleReserved(scheduleId: Long): Single<Boolean> =
        queries.isScheduleReserved(scheduleId)
            .asSingle(ioScheduler)
            .mapToOne()

    override fun insertCompletable(vararg obj: ReservedSchedule): Completable =
        {
            queries.transaction {
                obj.forEach {
                    queries.insert(it.toNewEntity())
                }
            }
        }
            .toCompletable()
            .subscribeOn(ioScheduler)

    override fun upsertCompletable(objects: List<ReservedSchedule>): Completable =
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

private fun ReservedSchedule.toNewEntity() =
    ReservedSchedules(id, datetime)
