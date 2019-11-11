package ru.olegivo.afs.favorites.data

import io.reactivex.Completable
import io.reactivex.Single
import ru.olegivo.afs.favorites.domain.models.FavoriteFilter

interface FavoritesDbSource {
    fun addFilter(favoriteFilter: FavoriteFilter): Completable
    fun getFavoriteFilters(): Single<List<FavoriteFilter>>
    fun removeFilter(favoriteFilter: FavoriteFilter): Completable
}
