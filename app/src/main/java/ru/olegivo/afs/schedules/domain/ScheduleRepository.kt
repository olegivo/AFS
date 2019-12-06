package ru.olegivo.afs.schedules.domain

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import ru.olegivo.afs.schedules.domain.models.Schedule
import ru.olegivo.afs.schedules.domain.models.Slot

interface ScheduleRepository {
    fun getCurrentWeekSchedule(clubId: Int): Maybe<List<Schedule>>
    fun getSlots(clubId: Int, ids: List<Long>): Single<List<Slot>>
    fun setScheduleReserved(schedule: Schedule): Completable
    fun getCurrentWeekReservedScheduleIds(): Single<List<Long>>
    fun actualizeSchedules(clubId: Int): Single<List<Schedule>>
    fun getSchedule(scheduleId: Long): Single<Schedule>
    fun isScheduleReserved(scheduleId: Long): Single<Boolean>
}
