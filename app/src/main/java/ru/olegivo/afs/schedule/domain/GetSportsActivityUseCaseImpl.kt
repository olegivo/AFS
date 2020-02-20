package ru.olegivo.afs.schedule.domain

import io.reactivex.Single
import ru.olegivo.afs.favorites.domain.FavoritesRepository
import ru.olegivo.afs.schedules.domain.ScheduleRepository
import ru.olegivo.afs.schedules.domain.models.SportsActivity
import javax.inject.Inject

class GetSportsActivityUseCaseImpl @Inject constructor(
    private val scheduleRepository: ScheduleRepository,
    private val favoritesRepository: FavoritesRepository
) :
    GetSportsActivityUseCase {
    override fun invoke(clubId: Int, scheduleId: Long): Single<SportsActivity> {
        return scheduleRepository.getSchedule(scheduleId)
            .flatMap { schedule ->
                scheduleRepository.getSlots(clubId, listOf(scheduleId))
                    .flatMap { slots ->
                        val availableSlots: Int? =
                            slots.singleOrNull { it.id == scheduleId }
                                ?.slots
                                ?: 0
                        scheduleRepository.isScheduleReserved(scheduleId)
                            .flatMap { isReserved ->
                                favoritesRepository.isFavorite(schedule)
                                    .map { isFavorite ->
                                        SportsActivity(
                                            schedule = schedule,
                                            availableSlots = availableSlots,
                                            isReserved = isReserved,
                                            isFavorite = isFavorite
                                        )
                                    }
                            }
                    }
            }
    }
}
