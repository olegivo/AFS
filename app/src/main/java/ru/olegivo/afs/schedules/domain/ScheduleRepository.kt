package ru.olegivo.afs.schedules.domain

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import ru.olegivo.afs.schedules.domain.models.Schedule
import ru.olegivo.afs.schedules.domain.models.Slot
import java.util.*

interface ScheduleRepository {
    fun getCurrentWeekSchedule(clubId: Int): Maybe<List<Schedule>>
    fun getDaySchedule(clubId: Int, day: Date): Maybe<List<Schedule>>
    fun getSlots(clubId: Int, ids: List<Long>): Single<List<Slot>>
    fun setScheduleReserved(schedule: Schedule): Completable
    fun getCurrentWeekReservedScheduleIds(): Single<List<Long>>
    fun getDayReservedScheduleIds(day: Date): Single<List<Long>>
    fun actualizeSchedules(clubId: Int): Single<List<Schedule>>
    fun getSchedule(scheduleId: Long): Single<Schedule>
    fun isScheduleReserved(scheduleId: Long): Single<Boolean>
    fun getSchedules(ids: List<Long>): Single<List<Schedule>>
}
