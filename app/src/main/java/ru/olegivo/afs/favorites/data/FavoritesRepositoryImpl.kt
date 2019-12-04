package ru.olegivo.afs.favorites.data

import io.reactivex.Completable
import io.reactivex.Single
import ru.olegivo.afs.favorites.domain.FavoritesRepository
import ru.olegivo.afs.favorites.domain.models.FavoriteFilter
import ru.olegivo.afs.favorites.domain.models.toFavoriteFilter
import ru.olegivo.afs.schedules.domain.models.Schedule
import javax.inject.Inject

class FavoritesRepositoryImpl @Inject constructor(private val favoritesDbSource: FavoritesDbSource) :
    FavoritesRepository {

    override fun getFavoriteFilters(): Single<List<FavoriteFilter>> =
        favoritesDbSource.getFavoriteFilters()

    override fun addFilter(favoriteFilter: FavoriteFilter): Completable =
        favoritesDbSource.addFilter(favoriteFilter)

    override fun removeFilter(favoriteFilter: FavoriteFilter): Completable =
        favoritesDbSource.removeFilter(favoriteFilter)

    override fun addReminderToRecord(schedule: Schedule): Completable {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun isFavorite(schedule: Schedule): Single<Boolean> =
        favoritesDbSource.exist(schedule.toFavoriteFilter())
}