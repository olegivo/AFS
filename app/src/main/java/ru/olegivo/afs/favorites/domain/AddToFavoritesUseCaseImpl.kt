package ru.olegivo.afs.favorites.domain

import io.reactivex.Completable
import ru.olegivo.afs.favorites.domain.models.toFavoriteFilter
import ru.olegivo.afs.schedules.domain.models.Schedule
import javax.inject.Inject

class AddToFavoritesUseCaseImpl @Inject constructor(private val favoritesRepository: FavoritesRepository) :
    AddToFavoritesUseCase {

    override fun invoke(schedule: Schedule): Completable = favoritesRepository.addFilter(
        schedule.toFavoriteFilter()
    )
}
