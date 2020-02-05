package ru.olegivo.afs.favorites.data

import io.reactivex.Completable
import io.reactivex.Single
import ru.olegivo.afs.favorites.domain.models.FavoriteFilter
import ru.olegivo.afs.schedules.domain.models.Schedule
import java.util.*

interface FavoritesDbSource {
    fun addFilter(favoriteFilter: FavoriteFilter): Completable
    fun getFavoriteFilters(): Single<List<FavoriteFilter>>
    fun removeFilter(favoriteFilter: FavoriteFilter): Completable
    fun exist(favoriteFilter: FavoriteFilter): Single<Boolean>
    fun getActiveRecordReminderSchedules(moment: Date): Single<List<Long>>
    fun addReminderToRecord(schedule: Schedule): Completable
    fun hasPlannedReminderToRecord(schedule: Schedule): Single<Boolean>
}
