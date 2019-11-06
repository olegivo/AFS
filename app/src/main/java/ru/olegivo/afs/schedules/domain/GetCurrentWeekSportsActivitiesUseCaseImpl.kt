package ru.olegivo.afs.schedules.domain

import io.reactivex.Scheduler
import io.reactivex.Single
import ru.olegivo.afs.schedules.domain.models.SportsActivity
import javax.inject.Inject
import javax.inject.Named

class GetCurrentWeekSportsActivitiesUseCaseImpl @Inject constructor(
    private val scheduleRepository: ScheduleRepository,
    @Named("computation") private val computationScheduler: Scheduler
) : GetCurrentWeekScheduleUseCase {

    override fun invoke(clubId: Int): Single<List<SportsActivity>> =
        scheduleRepository.getCurrentWeekSchedule(clubId).flatMap { schedules ->
            scheduleRepository.getSlots(clubId, schedules.map { it.id }).flatMap { slots ->
                val slotsById = slots.associate { it.id to it.slots }
                scheduleRepository.getCurrentWeekReservedScheduleIds()
                    .observeOn(computationScheduler)
                    .flatMap { currentWeekReservedScheduleIds ->
                        Single.just(
                            schedules.map {
                                SportsActivity(
                                    schedule = it,
                                    availableSlots = slotsById[it.id] ?: 0,
                                    isReserved = currentWeekReservedScheduleIds.contains(it.id)
                                )
                            }
                        )
                    }
            }
        }
}