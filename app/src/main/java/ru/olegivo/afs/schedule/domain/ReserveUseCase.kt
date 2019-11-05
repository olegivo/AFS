package ru.olegivo.afs.schedule.domain

import io.reactivex.Single
import ru.olegivo.afs.schedule.domain.models.ReserveResult
import ru.olegivo.afs.schedules.domain.models.Schedule

interface ReserveUseCase {
    fun reserve(schedule: Schedule, fio: String, phone: String): Single<out ReserveResult>
}
