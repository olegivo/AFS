package ru.olegivo.afs.favorites.data

import io.reactivex.Completable
import io.reactivex.Single
import ru.olegivo.afs.favorites.domain.FavoritesRepository
import ru.olegivo.afs.favorites.domain.models.FavoriteFilter
import ru.olegivo.afs.favorites.domain.models.toFavoriteFilter
import ru.olegivo.afs.schedules.domain.models.Schedule
import java.util.*
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
        return favoritesDbSource.addReminderToRecord(schedule)
    }

    override fun getActiveRecordReminderSchedules(moment: Date): Single<List<Long>> {
        return favoritesDbSource.getActiveRecordReminderSchedules(moment)
    }

    override fun isFavorite(schedule: Schedule): Single<Boolean> =
        favoritesDbSource.exist(schedule.toFavoriteFilter())
}