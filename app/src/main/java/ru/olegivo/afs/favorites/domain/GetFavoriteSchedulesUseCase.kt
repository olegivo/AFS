package ru.olegivo.afs.favorites.domain

import io.reactivex.Single
import ru.olegivo.afs.schedules.domain.models.Schedule

interface GetFavoriteSchedulesUseCase {
    operator fun invoke(schedules: List<Schedule>): Single<List<Long>>
}
