package ru.olegivo.afs.schedule.data

import io.reactivex.Completable
import ru.olegivo.afs.schedule.domain.models.Schedule

interface ScheduleDbSource {
    fun setScheduleReserved(schedule: Schedule): Completable
}
