package ru.olegivo.afs.schedules.data

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Scheduler
import io.reactivex.Single
import ru.olegivo.afs.common.add
import ru.olegivo.afs.common.domain.DateProvider
import ru.olegivo.afs.common.firstDayOfWeek
import ru.olegivo.afs.schedules.data.models.toDomain
import ru.olegivo.afs.schedules.domain.ScheduleRepository
import ru.olegivo.afs.schedules.domain.models.Schedule
import ru.olegivo.afs.schedules.domain.models.Slot
import java.util.*
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

    override fun getCurrentWeekSchedule(clubId: Int): Maybe<List<Schedule>> =
        withCurrentWeekInterval { weekStart, nextWeekStart ->
            scheduleDbSource.getSchedules(clubId, weekStart, nextWeekStart)
                .observeOn(computationScheduler)
                .map { schedules ->
                    schedules.map {
                        it.toDomain()
                    }
                }
        }

    override fun getSlots(clubId: Int, ids: List<Long>): Single<List<Slot>> =
        scheduleNetworkSource.getSlots(clubId, ids)


    override fun getCurrentWeekReservedScheduleIds(): Single<List<Long>> =
        withCurrentWeekInterval { weekStart, nextWeekStart ->
            scheduleDbSource.getReservedScheduleIds(weekStart, nextWeekStart)
        }

    override fun actualizeSchedules(clubId: Int): Completable =
        scheduleNetworkSource.getSchedule(clubId)
            .flatMapCompletable {
                scheduleDbSource.putSchedules(it)
            }

    private inline fun <T> withCurrentWeekInterval(block: (Date, Date) -> T): T {
        val now = dateProvider.getDate()
        val weekStart = firstDayOfWeek(now)
        val nextWeekStart = weekStart.add(days = 7)
        return block(weekStart, nextWeekStart)
    }
}