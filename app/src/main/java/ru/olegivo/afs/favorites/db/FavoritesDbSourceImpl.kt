package ru.olegivo.afs.favorites.db

import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.Single
import ru.olegivo.afs.favorites.data.FavoritesDbSource
import ru.olegivo.afs.favorites.db.modes.RecordReminderScheduleEntity
import ru.olegivo.afs.favorites.db.modes.toDb
import ru.olegivo.afs.favorites.db.modes.toDomain
import ru.olegivo.afs.favorites.domain.models.FavoriteFilter
import ru.olegivo.afs.schedules.domain.models.Schedule
import java.util.*
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

    override fun removeFilter(favoriteFilter: FavoriteFilter): Completable =
        with(favoriteFilter) { favoriteDao.removeFilter(group, activity, dayOfWeek, timeOfDay) }
            .subscribeOn(ioScheduler)

    override fun exist(favoriteFilter: FavoriteFilter): Single<Boolean> =
        with(favoriteFilter) { favoriteDao.exist(group, activity, dayOfWeek, timeOfDay) }
            .subscribeOn(ioScheduler)

    override fun getActiveRecordReminderSchedules(moment: Date): Single<List<Long>> {
        return favoriteDao.getActiveRecordReminderScheduleIds(moment)
            .subscribeOn(ioScheduler)
    }

    override fun addReminderToRecord(schedule: Schedule): Completable {
        return favoriteDao.addReminderToRecord(
            RecordReminderScheduleEntity(
                schedule.id,
                schedule.recordFrom!!,
                schedule.recordTo!!
            )
        ).subscribeOn(ioScheduler)
    }

    override fun getFavoriteFilters(): Single<List<FavoriteFilter>> =
        favoriteDao.getFavoriteFilters()
            .subscribeOn(ioScheduler)
            .observeOn(computationScheduler)
            .map { list -> list.map { it.toDomain() } }
}
