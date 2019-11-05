package ru.olegivo.afs.favorites.domain

import io.reactivex.Completable
import io.reactivex.Single
import ru.olegivo.afs.favorites.domain.models.FavoriteFilter

interface FavoritesRepository {
    fun addFilter(favoriteFilter: FavoriteFilter): Completable
    fun getFavoriteFilters(): Single<List<FavoriteFilter>>
}
