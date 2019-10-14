package ru.olegivo.afs.schedule.data

import io.reactivex.Single
import ru.olegivo.afs.schedule.data.models.DataSchedule
import ru.olegivo.afs.schedule.data.models.Slot
import ru.olegivo.afs.schedule.network.models.Schedules

interface ScheduleNetworkSource {

    fun getSchedules(clubId: Int): Single<Schedules>
    fun getSchedule(clubId: Int): Single<List<DataSchedule>>
    fun getSlots(clubId: Int, ids: List<Long>): Single<List<Slot>>
    fun getNextSchedule(schedules: Schedules): Schedules?
    fun getPrevSchedule(schedules: Schedules): Schedules?
}
