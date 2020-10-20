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
import ru.olegivo.afs.common.network.Api
import ru.olegivo.afs.schedules.data.ScheduleNetworkSource
import ru.olegivo.afs.schedules.data.models.DataSchedule
import ru.olegivo.afs.schedules.domain.models.Slot
import ru.olegivo.afs.shared.network.models.Schedules
import ru.olegivo.afs.schedules.network.models.toData
import javax.inject.Inject

class ScheduleNetworkSourceImpl @Inject constructor(
    private val api: Api
) : ScheduleNetworkSource {

    override suspend fun getSchedules(clubId: Int): Schedules =
        api.getSchedule(clubId)

    override suspend fun getSchedule(clubId: Int): List<DataSchedule> =
        getSchedules(clubId).schedule.map { it.toData(clubId) } // TODO: parallel map

    override suspend fun getSlots(clubId: Int, ids: List<Long>): List<Slot> {
        val idByPosition =
            ids.mapIndexed { index, id -> index.toString() to id.toString() }
                .associate { it }
        return api.getSlots(clubId, idByPosition).map { Slot(it.id, it.slots) }
    }

    override suspend fun getNextSchedule(schedules: Schedules): Schedules? =
        schedules.next?.let { getSchedules(it) }

    override suspend fun getPrevSchedule(schedules: Schedules): Schedules? =
        schedules.prev?.let { getSchedules(it) }

    private suspend fun getSchedules(url: String): Schedules? {
        val uri = Uri.parse(url)
        val path = uri.path!!.trimStart('/')
        return api.getSchedule(
            path = path,
            options = uri.queryParameterNames.associateBy({ it }, { uri.getQueryParameter(it)!! })
        )
    }
}
