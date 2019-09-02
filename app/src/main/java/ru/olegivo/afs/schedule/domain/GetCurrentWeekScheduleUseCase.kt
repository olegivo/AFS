package ru.olegivo.afs.schedule.domain

import io.reactivex.Single
import ru.olegivo.afs.schedule.domain.models.Schedule

interface GetCurrentWeekScheduleUseCase {
    operator fun invoke(clubId: Int): Single<List<Schedule>>
}