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

package ru.olegivo.afs.schedule.network

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import ru.olegivo.afs.clubs.network.ClubsNetworkSourceImpl
import ru.olegivo.afs.common.network.AuthorizedApiTest
import ru.olegivo.afs.schedule.data.ReserveNetworkSource
import ru.olegivo.afs.schedule.domain.models.Reserve
import ru.olegivo.afs.schedules.data.ScheduleNetworkSource
import ru.olegivo.afs.schedules.data.models.DataSchedule
import ru.olegivo.afs.schedules.domain.models.Slot
import ru.olegivo.afs.schedules.network.ScheduleNetworkSourceImpl
import ru.olegivo.afs.schedules.network.models.DomainClub
import ru.olegivo.afs.suite.NetworkTest

@NetworkTest
class ReserveNetworkSourceImplTest : AuthorizedApiTest() {

    @Test
    fun reserve_WHEN_the_available_slots_is_0() = runBlockingTest {

        val scheduleNetworkSource: ScheduleNetworkSource = ScheduleNetworkSourceImpl(api)
        val reserveNetworkSource: ReserveNetworkSource =
            ReserveNetworkSourceImpl(api, networkErrorsMapper, testScheduler)

        runBlocking {
            data class T(val scheduleIdWithZeroSlot: Long, val clubId: Int)
            getClubs().asSequence()
                .mapNotNull { club ->
                    val slots = runBlocking { getSlots(scheduleNetworkSource, club.id) }
                    val scheduleIdWithZeroSlot = slots.firstOrNull { it.slots ?: 0 == 0 }?.id
                    scheduleIdWithZeroSlot?.let { T(scheduleIdWithZeroSlot = it, clubId = club.id) }
                }
                .firstOrNull()
                ?.also {
                    // not every times has the sport activity with zero slot,
                    // so when it not null, we can check:
                    reserveNetworkSource.reserve(
                        Reserve(
                            fio = "Тестович А.Б.",
                            phone = "79817564213",
                            scheduleId = it.scheduleIdWithZeroSlot,
                            clubId = it.clubId
                        )
                    )
                }
        }
    }

    private suspend fun getSlots(
        scheduleNetworkSource: ScheduleNetworkSource,
        clubId: Int
    ): List<Slot> {
        val schedule = getSchedule(scheduleNetworkSource, clubId)
        return getSlots(schedule, scheduleNetworkSource, clubId)
    }

    private suspend fun getSlots(
        schedules: List<DataSchedule>,
        scheduleNetworkSource: ScheduleNetworkSource,
        clubId: Int
    ): List<Slot> {
        val sortedBy = schedules.sortedBy { it.datetime }
        val preEntrySchedules = sortedBy
            .filter { it.preEntry }
        //.filter { it.totalSlots == 0 }
        return scheduleNetworkSource.getSlots(clubId, preEntrySchedules.map { it.id })
    }

    private suspend fun getSchedule(
        scheduleNetworkSource: ScheduleNetworkSource,
        clubId: Int
    ): List<DataSchedule> = scheduleNetworkSource.getSchedule(clubId)

    private suspend fun getClubs(): List<DomainClub> =
        ClubsNetworkSourceImpl(api).getClubs()
}
