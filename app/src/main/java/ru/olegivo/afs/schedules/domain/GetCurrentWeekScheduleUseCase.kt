package ru.olegivo.afs.schedules.domain

import io.reactivex.Maybe
import ru.olegivo.afs.schedules.domain.models.SportsActivity

interface GetCurrentWeekScheduleUseCase {
    operator fun invoke(clubId: Int): Maybe<List<SportsActivity>>
}