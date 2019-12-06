package ru.olegivo.afs.schedule.domain

import io.reactivex.Single
import ru.olegivo.afs.schedules.domain.models.SportsActivity

interface GetSportsActivityUseCase {
    operator fun invoke(clubId: Int, scheduleId: Long): Single<SportsActivity>
}
