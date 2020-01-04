package ru.olegivo.afs.schedules.domain

import io.reactivex.Maybe
import ru.olegivo.afs.schedules.domain.models.SportsActivity
import java.util.*

interface GetDaySportsActivitiesUseCase {
    operator fun invoke(clubId: Int, day: Date): Maybe<List<SportsActivity>>
}