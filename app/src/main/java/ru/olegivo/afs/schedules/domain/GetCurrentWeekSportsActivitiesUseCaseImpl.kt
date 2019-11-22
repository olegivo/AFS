package ru.olegivo.afs.schedules.domain

import io.reactivex.Maybe
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.rxkotlin.Singles
import ru.olegivo.afs.favorites.domain.FavoritesRepository
import ru.olegivo.afs.favorites.domain.models.FavoriteFilter
import ru.olegivo.afs.favorites.domain.models.toFavoriteFilter
import ru.olegivo.afs.schedules.domain.models.Schedule
import ru.olegivo.afs.schedules.domain.models.Slot
import ru.olegivo.afs.schedules.domain.models.SportsActivity
import javax.inject.Inject
import javax.inject.Named

class GetCurrentWeekSportsActivitiesUseCaseImpl @Inject constructor(
    private val scheduleRepository: ScheduleRepository,
    private val favoritesRepository: FavoritesRepository,
    @Named("computation") private val computationScheduler: Scheduler
) : GetCurrentWeekScheduleUseCase {

    override fun invoke(clubId: Int): Maybe<List<SportsActivity>> =
        getCurrentWeekSchedule(clubId)
            .flatMap { schedules ->
                Singles.zip(
                    scheduleRepository.getSlots(clubId, schedules.map { it.id }),
                    scheduleRepository.getCurrentWeekReservedScheduleIds(),
                    getFavoritesScheduleIds(schedules)
                ) { slots,
                    currentWeekReservedScheduleIds,
                    favoritesScheduleIds ->
                    getSchedules(
                        slots,
                        schedules,
                        currentWeekReservedScheduleIds,
                        favoritesScheduleIds
                    )
                }.toMaybe()
            }

    private fun getSchedules(
        slots: List<Slot>,
        schedules: List<Schedule>,
        currentWeekReservedScheduleIds: List<Long>,
        favoritesScheduleIds: List<Long>
    ): List<SportsActivity> {
        val slotsById = slots.associate { it.id to it.slots }
        return schedules.map {
            SportsActivity(
                schedule = it,
                availableSlots = slotsById[it.id] ?: 0,
                isReserved = currentWeekReservedScheduleIds.contains(it.id),
                isFavorite = favoritesScheduleIds
                    .contains(it.id)
            )
        }
    }

    private fun getCurrentWeekSchedule(clubId: Int): Maybe<List<Schedule>> {
        return scheduleRepository.getCurrentWeekSchedule(clubId)
            .switchIfEmpty(Maybe.defer {
                scheduleRepository.actualizeSchedules(clubId)
                    .andThen(scheduleRepository.getCurrentWeekSchedule(clubId))
            })
    }


    private fun getFavoritesScheduleIds(schedules: List<Schedule>): Single<List<Long>> {
        return favoritesRepository.getFavoriteFilters()
            .observeOn(computationScheduler)
            .map { favoriteFilters ->
                schedules
                    .filter { schedule ->
                        applyFilters(schedule, favoriteFilters)
                    }
                    .map { it.id }
            }
    }

    private fun applyFilters(schedule: Schedule, favoriteFilters: List<FavoriteFilter>): Boolean =
        favoriteFilters.any { favoriteFilter ->
            favoriteFilter == schedule.toFavoriteFilter()
        }
}