package ru.olegivo.afs.schedule.data

import io.reactivex.Completable
import io.reactivex.Single
import ru.olegivo.afs.schedule.domain.ScheduleRepository
import ru.olegivo.afs.schedule.domain.models.Schedule
import javax.inject.Inject

class ScheduleRepositoryImpl @Inject constructor(
    private val scheduleNetworkSource: ScheduleNetworkSource,
    private val scheduleDbSource: ScheduleDbSource
) :
    ScheduleRepository {
    override fun setScheduleReserved(schedule: Schedule): Completable =
        scheduleDbSource.setScheduleReserved(schedule)

    override fun getCurrentWeekSchedule(clubId: Int): Single<List<Schedule>> =
        scheduleNetworkSource.getSchedule(clubId).flatMap { schedules ->
            scheduleNetworkSource.getSlots(clubId, schedules.map { it.id })
                .flatMap { slots ->
                    val slotsById = slots.associate { it.id to it.slots }
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
                                    availableSlots = slotsById[id] ?: 0
                                )
                            }
                        })
                }
        }
}