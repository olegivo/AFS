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

package ru.olegivo.afs.schedules.data

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Scheduler
import io.reactivex.Single
import ru.olegivo.afs.common.CoroutineToRxAdapter
import ru.olegivo.afs.common.add
import ru.olegivo.afs.common.domain.DateProvider
import ru.olegivo.afs.common.firstDayOfWeek
import ru.olegivo.afs.extensions.mapList
import ru.olegivo.afs.extensions.parallelMapList
import ru.olegivo.afs.extensions.toSingle
import ru.olegivo.afs.favorites.domain.models.FavoriteFilter
import ru.olegivo.afs.schedules.data.models.DataSchedule
import ru.olegivo.afs.schedules.data.models.toDomain
import ru.olegivo.afs.schedules.domain.ScheduleRepository
import ru.olegivo.afs.schedules.domain.models.Schedule
import ru.olegivo.afs.schedules.domain.models.Slot
import java.util.Date
import javax.inject.Inject
import javax.inject.Named

class ScheduleRepositoryImpl @Inject constructor(
    private val scheduleNetworkSource: ScheduleNetworkSource,
    private val scheduleDbSource: ScheduleDbSource,
    private val dateProvider: DateProvider,
    private val coroutineToRxAdapter: CoroutineToRxAdapter,
    @Named("computation") private val computationScheduler: Scheduler
) :
    ScheduleRepository {
    override fun setScheduleReserved(schedule: Schedule): Completable =
        scheduleDbSource.setScheduleReserved(schedule)

    override fun getCurrentWeekSchedule(clubId: Int): Maybe<List<Schedule>> =
        withCurrentWeekInterval { weekStart, nextWeekStart ->
            scheduleDbSource.getSchedules(clubId, weekStart, nextWeekStart)
                .toDomain()
        }

    override fun getDaySchedule(clubId: Int, day: Date): Maybe<List<Schedule>> =
        withDayInterval(day) { dayStart, nextDayStart ->
            scheduleDbSource.getSchedules(clubId, dayStart, nextDayStart)
                .toDomain()
        }

    override fun getSlots(clubId: Int, ids: List<Long>): Single<List<Slot>> =
        coroutineToRxAdapter.runToSingle { scheduleNetworkSource.getSlots(clubId, ids) }

    override fun getCurrentWeekReservedScheduleIds(): Single<List<Long>> =
        withCurrentWeekInterval { weekStart, nextWeekStart ->
            scheduleDbSource.getReservedScheduleIds(weekStart, nextWeekStart)
        }

    override fun getDayReservedScheduleIds(day: Date): Single<List<Long>> =
        withDayInterval(day) { dayStart, nextDayStart ->
            scheduleDbSource.getReservedScheduleIds(dayStart, nextDayStart)
        }

    override fun actualizeSchedules(clubId: Int): Single<List<Schedule>> =
        coroutineToRxAdapter.runToSingle { scheduleNetworkSource.getSchedule(clubId) }
            .flatMap { list ->
                val schedules = list.toSingle().toDomain()
                scheduleDbSource.putSchedules(list)
                    .andThen(schedules)
            }

    override fun getSchedules(ids: List<Long>): Single<List<Schedule>> {
        return scheduleDbSource.getSchedules(ids).toDomain()
    }

    override fun filterSchedules(
        favoriteFilter: FavoriteFilter,
        clubId: Int
    ): Single<List<Schedule>> =
        scheduleDbSource.filterSchedules(favoriteFilter, clubId)
            .mapList { it.toDomain() }

    override fun getSchedule(scheduleId: Long): Single<Schedule> =
        scheduleDbSource.getSchedule(scheduleId)
            .observeOn(computationScheduler)
            .map { it.toDomain() }

    override fun isScheduleReserved(scheduleId: Long): Single<Boolean> =
        scheduleDbSource.isScheduleReserved(scheduleId)

    private fun Single<List<DataSchedule>>.toDomain() =
        parallelMapList(computationScheduler) { it.toDomain() }

    private fun Maybe<List<DataSchedule>>.toDomain() =
        parallelMapList(computationScheduler) { it.toDomain() }

    private inline fun <T> withCurrentWeekInterval(block: (Date, Date) -> T): T {
        val now = dateProvider.getDate()
        val weekStart = firstDayOfWeek(now)
        val nextWeekStart = weekStart.add(days = 7)
        return block(weekStart, nextWeekStart)
    }

    private inline fun <T> withDayInterval(day: Date, block: (Date, Date) -> T): T {
        return block(day, day.add(days = 1))
    }
}
