package ru.olegivo.afs.schedules.domain

import io.reactivex.Single
import ru.olegivo.afs.schedules.domain.models.Schedule

interface GetCurrentWeekScheduleUseCase {
    operator fun invoke(clubId: Int): Single<List<Schedule>>
}