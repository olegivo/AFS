package ru.olegivo.afs.schedules.data

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import ru.olegivo.afs.schedules.data.models.DataSchedule
import ru.olegivo.afs.schedules.domain.models.Schedule
import java.util.*

interface ScheduleDbSource {
    fun setScheduleReserved(schedule: Schedule): Completable
    fun getReservedScheduleIds(from: Date, until: Date): Single<List<Long>>
    fun getSchedules(clubId: Int, from: Date, until: Date): Maybe<List<DataSchedule>>
    fun putSchedules(schedules: List<DataSchedule>): Completable
}
