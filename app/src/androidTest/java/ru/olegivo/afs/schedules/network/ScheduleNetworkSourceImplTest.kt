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

import android.util.Log
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import ru.olegivo.afs.clubs.network.ClubsNetworkSourceImpl
import ru.olegivo.afs.common.network.AuthorizedApiTest
import ru.olegivo.afs.schedules.data.ScheduleNetworkSource
import ru.olegivo.afs.schedules.network.models.DomainClub
import ru.olegivo.afs.shared.network.models.Schedules
import ru.olegivo.afs.suite.NetworkTest

@NetworkTest
class ScheduleNetworkSourceImplTest : AuthorizedApiTest() {

    @Test
    fun getSchedule_all_clubs() = runBlockingTest {
        val scheduleNetworkSource = ScheduleNetworkSourceImpl(api)
        val schedules = runBlocking {
            val clubs = getClubs()
            clubs.map { club ->
                val schedule = getSchedule(scheduleNetworkSource, club.id)
                schedule
            }
        }

        assertThat(schedules).isNotEmpty
        assertThat(schedules).matches { it.isNotEmpty() }
    }

    private suspend fun getSchedule(
        scheduleNetworkSource: ScheduleNetworkSourceImpl,
        clubId: Int
    ) = scheduleNetworkSource
        .getSchedule(clubId)

    @Test
    fun getSchedule_flatten_next() = runBlockingTest {
        val scheduleNetworkSource: ScheduleNetworkSource =
            ScheduleNetworkSourceImpl(api)

        val schedulesCurrentWeek = getSchedulesCurrentWeek(scheduleNetworkSource)
        var current: Schedules? = schedulesCurrentWeek
        var count = 0
        while (current != null && count < 3) {
            Log.d("getSchedule_flatten", "next = ${current.next}")
            current = runBlocking { scheduleNetworkSource.getNextSchedule(current!!) }
            count++
        }
    }

    @Test
    fun getSchedule_flatten_prev() = runBlockingTest {
        val scheduleNetworkSource: ScheduleNetworkSource =
            ScheduleNetworkSourceImpl(api)

        val schedulesCurrentWeek = getSchedulesCurrentWeek(scheduleNetworkSource)
        var current: Schedules? = schedulesCurrentWeek
        var count = 0
        while (current != null && count < 3) {
            Log.d("getSchedule_flatten", "prev = ${current.prev}")
            current = runBlocking { scheduleNetworkSource.getPrevSchedule(current!!) }
            count++
        }
    }

    private fun getSchedulesCurrentWeek(scheduleNetworkSource: ScheduleNetworkSource): Schedules =
        runBlocking {
            val clubs = getClubs()
            scheduleNetworkSource.getSchedules(clubs.first().id)
        }

    private suspend fun getClubs(): List<DomainClub> {
        val clubsNetworkSourceImpl = ClubsNetworkSourceImpl(
            api = api/*,
                ioScheduler = scheduler,
                ioDispatcher = Dispatchers.Main*/
        )

        return clubsNetworkSourceImpl.getClubs()
    }
}
