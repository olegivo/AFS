package ru.olegivo.afs.schedule.network

import android.net.Uri
import io.reactivex.Scheduler
import io.reactivex.Single
import ru.olegivo.afs.common.network.Api
import ru.olegivo.afs.schedule.data.ScheduleNetworkSource
import ru.olegivo.afs.schedule.network.models.Schedules
import javax.inject.Inject
import javax.inject.Named

class ScheduleNetworkSourceImpl @Inject constructor(
    private val api: Api,
    @Named("io") private val ioScheduler: Scheduler
) : ScheduleNetworkSource {
    override fun getSchedule(clubId: Int): Single<Schedules> {
        return api.getSchedule(clubId).subscribeOn(ioScheduler)
    }

    override fun getNextSchedule(schedules: Schedules): Schedules? =
        schedules.next?.let { getSchedules(it) }

    override fun getPrevSchedule(schedules: Schedules): Schedules? =
        schedules.prev?.let { getSchedules(it) }

    private fun getSchedules(url: String): Schedules? {
        val uri = Uri.parse(url)
        val path = uri.path!!.trimStart('/')
        return api.getSchedule(
            path,
            uri.queryParameterNames.associateBy({ it }, { uri.getQueryParameter(it) })
        ).blockingGet()
    }
}