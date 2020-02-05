package ru.olegivo.afs.favorites.domain

import io.reactivex.Completable
import io.reactivex.Single
import ru.olegivo.afs.favorites.domain.models.FavoriteFilter
import ru.olegivo.afs.schedules.domain.models.Schedule
import java.util.*

interface FavoritesRepository {
    fun addFilter(favoriteFilter: FavoriteFilter): Completable
    fun getFavoriteFilters(): Single<List<FavoriteFilter>>
    fun removeFilter(favoriteFilter: FavoriteFilter): Completable
    fun isFavorite(schedule: Schedule): Single<Boolean>
    fun addReminderToRecord(schedule: Schedule): Completable
    fun getActiveRecordReminderSchedules(moment: Date): Single<List<Long>>
    fun hasPlannedReminderToRecord(schedule: Schedule): Single<Boolean>
}
