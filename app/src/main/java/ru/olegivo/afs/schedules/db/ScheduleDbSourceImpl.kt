package ru.olegivo.afs.schedules.db

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Scheduler
import io.reactivex.Single
import ru.olegivo.afs.extensions.toSingle
import ru.olegivo.afs.schedules.data.ScheduleDbSource
import ru.olegivo.afs.schedules.data.models.DataSchedule
import ru.olegivo.afs.schedules.db.models.ReservedSchedule
import ru.olegivo.afs.schedules.db.models.toData
import ru.olegivo.afs.schedules.db.models.toDb
import ru.olegivo.afs.schedules.domain.models.Schedule
import java.util.*
import javax.inject.Inject
import javax.inject.Named

class ScheduleDbSourceImpl @Inject constructor(
    private val reserveDao: ReserveDao,
    private val scheduleDao: ScheduleDao,
    @Named("io") private val ioScheduler: Scheduler,
    @Named("computation") private val computationScheduler: Scheduler
) : ScheduleDbSource {
    override fun setScheduleReserved(schedule: Schedule): Completable =
        reserveDao.addReservedSchedule(ReservedSchedule(schedule.id, schedule.datetime))
            .subscribeOn(ioScheduler)

    override fun getReservedScheduleIds(from: Date, until: Date): Single<List<Long>> =
        reserveDao.getReservedScheduleIds(from, until)
            .subscribeOn(ioScheduler)

    override fun getSchedules(clubId: Int, from: Date, until: Date): Maybe<List<DataSchedule>> =
        scheduleDao.getSchedules(clubId, from, until)
            .subscribeOn(ioScheduler)
            .observeOn(computationScheduler)
            .flatMap { list ->
                if (list.isEmpty()) {
                    Maybe.empty<List<DataSchedule>>()
                } else {
                    Maybe.just(list.map { it.toData() })
                }
            }

    override fun putSchedules(schedules: List<DataSchedule>): Completable =
        schedules.toSingle()
            .subscribeOn(computationScheduler)
            .map { list -> list.map { it.toDb() } }
            .observeOn(ioScheduler)
            .flatMapCompletable {
                scheduleDao.putSchedules(it)
            }
}
