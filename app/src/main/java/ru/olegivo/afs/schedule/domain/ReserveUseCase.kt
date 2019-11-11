package ru.olegivo.afs.schedule.domain

import io.reactivex.Single
import ru.olegivo.afs.schedule.domain.models.ReserveResult
import ru.olegivo.afs.schedules.domain.models.SportsActivity

interface ReserveUseCase {
    fun reserve(
        sportsActivity: SportsActivity,
        fio: String,
        phone: String,
        hasAcceptedAgreement: Boolean
    ): Single<out ReserveResult>
}
