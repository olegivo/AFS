package ru.olegivo.afs.favorites.data

import io.reactivex.Completable
import io.reactivex.Single
import ru.olegivo.afs.favorites.domain.FavoritesRepository
import ru.olegivo.afs.favorites.domain.models.FavoriteFilter
import javax.inject.Inject

class FavoritesRepositoryImpl @Inject constructor(private val favoritesDbSource: FavoritesDbSource) :
    FavoritesRepository {

    override fun getFavoriteFilters(): Single<List<FavoriteFilter>> =
        favoritesDbSource.getFavoriteFilters()

    override fun addFilter(favoriteFilter: FavoriteFilter): Completable =
        favoritesDbSource.addFilter(favoriteFilter)

}