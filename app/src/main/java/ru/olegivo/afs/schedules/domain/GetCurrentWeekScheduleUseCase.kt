package ru.olegivo.afs.schedules.domain

import io.reactivex.Single
import ru.olegivo.afs.schedules.domain.models.SportsActivity

interface GetCurrentWeekScheduleUseCase {
    operator fun invoke(clubId: Int): Single<List<SportsActivity>>
}