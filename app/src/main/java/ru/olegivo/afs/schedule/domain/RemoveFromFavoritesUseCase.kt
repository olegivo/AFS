package ru.olegivo.afs.schedule.domain

import io.reactivex.Completable
import ru.olegivo.afs.schedules.domain.models.Schedule

interface RemoveFromFavoritesUseCase {
    operator fun invoke(schedule: Schedule): Completable
}
