package ru.olegivo.afs.schedule.db

import io.reactivex.Completable
import io.reactivex.Scheduler
import ru.olegivo.afs.schedule.data.ScheduleDbSource
import ru.olegivo.afs.schedule.db.models.ReservedSchedule
import ru.olegivo.afs.schedule.domain.models.Schedule
import javax.inject.Inject
import javax.inject.Named

class ScheduleDbSourceImpl @Inject constructor(
    private val scheduleDao: ScheduleDao,
    @Named("io") private val ioScheduler: Scheduler
) : ScheduleDbSource {

    override fun setScheduleReserved(schedule: Schedule): Completable =
        scheduleDao.addReservedSchedule(ReservedSchedule(schedule.id, schedule.datetime))
            .subscribeOn(ioScheduler)

}
