package ru.olegivo.afs.schedule.network

import android.net.Uri
import io.reactivex.Scheduler
import io.reactivex.Single
import ru.olegivo.afs.common.network.Api
import ru.olegivo.afs.schedule.data.ScheduleNetworkSource
import ru.olegivo.afs.schedule.data.models.DataSchedule
import ru.olegivo.afs.schedule.data.models.Slot
import ru.olegivo.afs.schedule.network.models.Schedules
import javax.inject.Inject
import javax.inject.Named

class ScheduleNetworkSourceImpl @Inject constructor(
    private val api: Api,
    @Named("io") private val ioScheduler: Scheduler,
    @Named("computation") private val computationScheduler: Scheduler
) : ScheduleNetworkSource {
    override fun getSchedules(clubId: Int): Single<Schedules> {
        return api.getSchedule(clubId).subscribeOn(ioScheduler)
    }

    override fun getSlots(clubId: Int, ids: List<Long>): Single<List<Slot>> {
        val idByPosition =
            ids.mapIndexed { index, id -> index.toString() to id.toString() }
                .associate { it }
        return api.getSlots(clubId, idByPosition)
            .subscribeOn(ioScheduler)
            .observeOn(computationScheduler)
            .map { slots ->
                slots.map { Slot(it.id, it.slots) }
            }
    }

    override fun getSchedule(clubId: Int): Single<List<DataSchedule>> {
        return getSchedules(clubId)
            .observeOn(computationScheduler)
            .map { schedules ->
                schedules.schedule.map {
                    DataSchedule(
                        it.id,
                        it.group.title,
                        it.activity.title,
                        it.datetime,
                        it.length,
                        it.room?.title,
                        it.trainers.firstOrNull()?.title,
                        it.preEntry,
                        it.totalSlots
                    ) // TODO: mapper
                }
            }
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