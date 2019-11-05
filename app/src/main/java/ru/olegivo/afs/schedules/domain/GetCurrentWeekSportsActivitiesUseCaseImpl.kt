package ru.olegivo.afs.schedules.domain

import io.reactivex.Scheduler
import io.reactivex.Single
import ru.olegivo.afs.favorites.domain.FavoritesRepository
import ru.olegivo.afs.favorites.domain.models.FavoriteFilter
import ru.olegivo.afs.favorites.domain.models.toFavoriteFilter
import ru.olegivo.afs.schedules.domain.models.Schedule
import ru.olegivo.afs.schedules.domain.models.SportsActivity
import javax.inject.Inject
import javax.inject.Named

class GetCurrentWeekSportsActivitiesUseCaseImpl @Inject constructor(
    private val scheduleRepository: ScheduleRepository,
    private val favoritesRepository: FavoritesRepository,
    @Named("computation") private val computationScheduler: Scheduler
) : GetCurrentWeekScheduleUseCase {

    override fun invoke(clubId: Int): Single<List<SportsActivity>> =
        scheduleRepository.getCurrentWeekSchedule(clubId).flatMap { schedules ->
            scheduleRepository.getSlots(clubId, schedules.map { it.id }).flatMap { slots ->
                val slotsById = slots.associate { it.id to it.slots }
                scheduleRepository.getCurrentWeekReservedScheduleIds()
                    .observeOn(computationScheduler)
                    .flatMap { currentWeekReservedScheduleIds ->
                        getFavoritesScheduleIds(schedules)
                            .flatMap { favoritesScheduleIds ->
                                Single.just(
                                    schedules.map {
                                        SportsActivity(
                                            schedule = it,
                                            availableSlots = slotsById[it.id] ?: 0,
                                            isReserved = currentWeekReservedScheduleIds.contains(it.id),
                                            isFavorite = favoritesScheduleIds.contains(it.id)
                                        )
                                    }
                                )
                            }
                    }
            }
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