package ru.olegivo.afs.schedules.data

import io.reactivex.Single
import ru.olegivo.afs.schedules.data.models.DataSchedule
import ru.olegivo.afs.schedules.domain.models.Slot
import ru.olegivo.afs.schedules.network.models.Schedules

interface ScheduleNetworkSource {

    fun getSchedules(clubId: Int): Single<Schedules>
    fun getSchedule(clubId: Int): Single<List<DataSchedule>>
    fun getSlots(clubId: Int, ids: List<Long>): Single<List<Slot>>
    fun getNextSchedule(schedules: Schedules): Schedules?
    fun getPrevSchedule(schedules: Schedules): Schedules?
}
