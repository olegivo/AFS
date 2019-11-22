package ru.olegivo.afs.schedules.data

import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.Single
import ru.olegivo.afs.common.add
import ru.olegivo.afs.common.domain.DateProvider
import ru.olegivo.afs.common.firstDayOfWeek
import ru.olegivo.afs.schedules.data.models.toDomain
import ru.olegivo.afs.schedules.domain.ScheduleRepository
import ru.olegivo.afs.schedules.domain.models.Schedule
import ru.olegivo.afs.schedules.domain.models.Slot
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
        scheduleNetworkSource.getSchedule(clubId)
            .observeOn(computationScheduler)
            .map { schedules ->
                schedules.map {
                    it.toDomain(clubId)
                }
            }

    override fun getSlots(clubId: Int, ids: List<Long>): Single<List<Slot>> =
        scheduleNetworkSource.getSlots(clubId, ids)

    override fun getCurrentWeekReservedScheduleIds(): Single<List<Long>> {
        val now = dateProvider.getDate()
        val weekStart = firstDayOfWeek(now)
        return scheduleDbSource.getReservedScheduleIds(
            weekStart,
            weekStart.add(days = 7)
        )
    }
}