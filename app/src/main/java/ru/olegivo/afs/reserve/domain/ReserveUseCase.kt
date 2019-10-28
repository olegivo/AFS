package ru.olegivo.afs.reserve.domain

import io.reactivex.Single
import ru.olegivo.afs.reserve.domain.models.ReserveResult
import ru.olegivo.afs.schedule.domain.models.Schedule

interface ReserveUseCase {
    fun reserve(schedule: Schedule, fio: String, phone: String): Single<out ReserveResult>
}
