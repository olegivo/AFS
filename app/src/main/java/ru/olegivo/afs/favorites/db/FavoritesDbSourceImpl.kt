package ru.olegivo.afs.favorites.db

import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.Single
import ru.olegivo.afs.favorites.data.FavoritesDbSource
import ru.olegivo.afs.favorites.db.modes.toDb
import ru.olegivo.afs.favorites.db.modes.toDomain
import ru.olegivo.afs.favorites.domain.models.FavoriteFilter
import javax.inject.Inject
import javax.inject.Named

class FavoritesDbSourceImpl @Inject constructor(
    private val favoriteDao: FavoriteDao,
    @Named("io") private val ioScheduler: Scheduler,
    @Named("computation") private val computationScheduler: Scheduler
) : FavoritesDbSource {

    override fun addFilter(favoriteFilter: FavoriteFilter): Completable =
        Single.fromCallable { favoriteFilter.toDb() }
            .subscribeOn(computationScheduler)
            .observeOn(ioScheduler)
            .flatMapCompletable {
                favoriteDao.addFilter(it)
            }

    override fun getFavoriteFilters(): Single<List<FavoriteFilter>> =
        favoriteDao.getFavoriteFilters()
            .subscribeOn(ioScheduler)
            .observeOn(computationScheduler)
            .map { list -> list.map { it.toDomain() } }
}
