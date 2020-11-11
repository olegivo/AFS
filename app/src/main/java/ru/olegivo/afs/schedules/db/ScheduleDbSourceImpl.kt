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

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Scheduler
import io.reactivex.Single
import ru.olegivo.afs.extensions.parallelMapList
import ru.olegivo.afs.extensions.toSingle
import ru.olegivo.afs.schedules.data.ScheduleDbSource
import ru.olegivo.afs.schedules.data.models.DataSchedule
import ru.olegivo.afs.schedules.db.models.ReservedSchedule
import ru.olegivo.afs.schedules.db.models.toData
import ru.olegivo.afs.schedules.db.models.toDb
import ru.olegivo.afs.schedules.domain.models.Schedule
import java.util.Date
import javax.inject.Inject
import javax.inject.Named

class ScheduleDbSourceImpl @Inject constructor(
    private val reserveDao: ReserveDao,
    private val scheduleDao: ScheduleDao,
    @Named("io") private val ioScheduler: Scheduler,
    @Named("computation") private val computationScheduler: Scheduler
) : ScheduleDbSource {

    override fun setScheduleReserved(schedule: Schedule): Completable =
        reserveDao.insert(ReservedSchedule(schedule.id, schedule.datetime))
            .subscribeOn(ioScheduler)

    override fun getReservedScheduleIds(from: Date, until: Date): Single<List<Long>> =
        reserveDao.getReservedScheduleIds(from, until)
            .subscribeOn(ioScheduler)

    override fun getSchedules(clubId: Int, from: Date, until: Date): Maybe<List<DataSchedule>> =
        scheduleDao.getSchedules(clubId, from, until)
            .subscribeOn(ioScheduler)
            .parallelMapList(computationScheduler) { it.toData() }

    override fun getSchedules(ids: List<Long>): Single<List<DataSchedule>> =
        scheduleDao.getSchedules(ids)
            .subscribeOn(ioScheduler)
            .parallelMapList(computationScheduler) { it.toData() }

    override fun putSchedules(schedules: List<DataSchedule>): Completable =
        schedules.toSingle()
            .parallelMapList(computationScheduler) { it.toDb() }
            .observeOn(ioScheduler)
            .flatMapCompletable {
                scheduleDao.upsert(it)
            }

    override fun getSchedule(id: Long): Single<DataSchedule> =
        scheduleDao.getSchedule(id)
            .subscribeOn(ioScheduler)
            .observeOn(computationScheduler)
            .map { it.toData() }

    override fun isScheduleReserved(scheduleId: Long): Single<Boolean> =
        reserveDao.isScheduleReserved(scheduleId)
            .subscribeOn(ioScheduler)
}
