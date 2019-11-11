package ru.olegivo.afs.schedule.domain

import io.reactivex.Completable
import ru.olegivo.afs.favorites.domain.FavoritesRepository
import ru.olegivo.afs.favorites.domain.models.toFavoriteFilter
import ru.olegivo.afs.schedules.domain.models.Schedule
import javax.inject.Inject

class RemoveFromFavoritesUseCaseImpl @Inject constructor(private val favoritesRepository: FavoritesRepository) : RemoveFromFavoritesUseCase {
    override fun invoke(schedule: Schedule): Completable =
        favoritesRepository.removeFilter(schedule.toFavoriteFilter())
}