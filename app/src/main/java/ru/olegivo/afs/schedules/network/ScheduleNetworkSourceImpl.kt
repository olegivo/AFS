/*
 * Copyright (C) 2020 Oleg Ivashchenko <olegivo@gmail.com>
 *
 * This file is part of AFS.
 *
 * AFS is free software: you can redistribute it and/or modify
 * it under the terms of the MIT License.
 *
 * AFS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * AFS.
 */

package ru.olegivo.afs.schedules.network

import android.net.Uri
import io.reactivex.Scheduler
import io.reactivex.Single
import ru.olegivo.afs.common.network.Api
import ru.olegivo.afs.extensions.parallelMap
import ru.olegivo.afs.schedules.data.ScheduleNetworkSource
import ru.olegivo.afs.schedules.data.models.DataSchedule
import ru.olegivo.afs.schedules.domain.models.Slot
import ru.olegivo.afs.schedules.network.models.Schedules
import ru.olegivo.afs.schedules.network.models.toData
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

    override fun getSchedule(clubId: Int): Single<List<DataSchedule>> {
        return getSchedules(clubId)
            .parallelMap(computationScheduler, { it.schedule }, { it.toData(clubId) })
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

    override fun getNextSchedule(schedules: Schedules): Schedules? =
        schedules.next?.let { getSchedules(it) }

    override fun getPrevSchedule(schedules: Schedules): Schedules? =
        schedules.prev?.let { getSchedules(it) }

    private fun getSchedules(url: String): Schedules? {
        val uri = Uri.parse(url)
        val path = uri.path!!.trimStart('/')
        return api.getSchedule(
            path,
            uri.queryParameterNames.associateBy({ it }, { uri.getQueryParameter(it)!! })
        ).blockingGet()
    }
}
