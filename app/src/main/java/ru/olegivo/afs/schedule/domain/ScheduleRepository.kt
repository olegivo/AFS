package ru.olegivo.afs.schedule.domain

import io.reactivex.Completable
import io.reactivex.Single
import ru.olegivo.afs.schedule.domain.models.Schedule

interface ScheduleRepository {
    fun getCurrentWeekSchedule(clubId: Int): Single<List<Schedule>>
    fun setScheduleReserved(schedule: Schedule): Completable
}
