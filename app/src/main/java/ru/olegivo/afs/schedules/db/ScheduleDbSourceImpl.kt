package ru.olegivo.afs.schedules.db

import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.Single
import ru.olegivo.afs.schedules.data.ScheduleDbSource
import ru.olegivo.afs.schedules.db.models.ReservedSchedule
import ru.olegivo.afs.schedules.domain.models.Schedule
import java.util.*
import javax.inject.Inject
import javax.inject.Named

class ScheduleDbSourceImpl @Inject constructor(
    private val reserveDao: ReserveDao,
    @Named("io") private val ioScheduler: Scheduler
) : ScheduleDbSource {
    override fun setScheduleReserved(schedule: Schedule): Completable =
        reserveDao.addReservedSchedule(ReservedSchedule(schedule.id, schedule.datetime))
            .subscribeOn(ioScheduler)

    override fun getReservedScheduleIds(from: Date, until: Date): Single<List<Long>> =
        reserveDao.getReservedScheduleIds(from, until)
            .subscribeOn(ioScheduler)

}
