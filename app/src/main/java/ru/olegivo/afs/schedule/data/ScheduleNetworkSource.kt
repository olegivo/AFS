package ru.olegivo.afs.schedule.data

import io.reactivex.Single
import ru.olegivo.afs.clubs.domain.models.Club
import ru.olegivo.afs.schedule.domain.models.Schedule
import ru.olegivo.afs.schedule.network.models.Schedules

interface ScheduleNetworkSource {

    fun getSchedules(clubId: Int): Single<Schedules>
    fun getSchedule(clubId: Int): Single<List<Schedule>>
    fun getNextSchedule(schedules: Schedules): Schedules?
    fun getPrevSchedule(schedules: Schedules): Schedules?
}
