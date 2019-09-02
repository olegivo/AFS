package ru.olegivo.afs.schedule.data

import io.reactivex.Single
import ru.olegivo.afs.schedule.domain.ScheduleRepository
import ru.olegivo.afs.schedule.domain.models.Schedule
import javax.inject.Inject

class ScheduleRepositoryImpl @Inject constructor(
    private val scheduleNetworkSource: ScheduleNetworkSource
) :
    ScheduleRepository {
    override fun getCurrentWeekSchedule(clubId: Int): Single<List<Schedule>> =
        scheduleNetworkSource.getSchedule(clubId)
}