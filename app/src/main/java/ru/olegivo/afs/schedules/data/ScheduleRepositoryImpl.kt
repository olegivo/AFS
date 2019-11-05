package ru.olegivo.afs.schedules.data

import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.Single
import ru.olegivo.afs.common.add
import ru.olegivo.afs.common.domain.DateProvider
import ru.olegivo.afs.common.firstDayOfWeek
import ru.olegivo.afs.schedules.domain.ScheduleRepository
import ru.olegivo.afs.schedules.domain.models.Schedule
import javax.inject.Inject
import javax.inject.Named

class ScheduleRepositoryImpl @Inject constructor(
    private val scheduleNetworkSource: ScheduleNetworkSource,
    private val scheduleDbSource: ScheduleDbSource,
    private val dateProvider: DateProvider,
    @Named("computation") private val computationScheduler: Scheduler
) :
    ScheduleRepository {
    override fun setScheduleReserved(schedule: Schedule): Completable =
        scheduleDbSource.setScheduleReserved(schedule)

    override fun getCurrentWeekSchedule(clubId: Int): Single<List<Schedule>> =
        scheduleNetworkSource.getSchedule(clubId).flatMap { schedules ->
            scheduleNetworkSource.getSlots(clubId, schedules.map { it.id })
                .flatMap { slots ->
                    val slotsById = slots.associate { it.id to it.slots }
                    getCurrentWeekReservedScheduleIds()
                        .observeOn(computationScheduler)
                        .flatMap { currentWeekReservedScheduleIds ->
                            Single.just(
                                schedules.map {
                                    with(it) {
                                        Schedule( // TODO: mapper
                                            id = id,
                                            clubId = clubId,
                                            group = group,
                                            activity = activity,
                                            datetime = datetime,
                                            length = length,
                                            room = room,
                                            trainer = trainer,
                                            preEntry = preEntry,
                                            totalSlots = totalSlots,
                                            availableSlots = slotsById[id] ?: 0,
                                            isReserved = currentWeekReservedScheduleIds.contains(id)
                                        )
                                    }
                                })
                        }

                }
        }

    private fun getCurrentWeekReservedScheduleIds(): Single<List<Long>> {
        val now = dateProvider.getDate()
        val weekStart = firstDayOfWeek(now)
        return scheduleDbSource.getReservedScheduleIds(weekStart, weekStart.add(days = 7))
    }
}