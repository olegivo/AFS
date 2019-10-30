package ru.olegivo.afs.schedule.data

import io.reactivex.Completable
import io.reactivex.Single
import ru.olegivo.afs.schedule.domain.models.Schedule
import java.util.*

interface ScheduleDbSource {
    fun setScheduleReserved(schedule: Schedule): Completable
    fun getReservedScheduleIds(from: Date, until: Date): Single<List<Long>>
}
