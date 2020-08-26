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

package ru.olegivo.afs.schedules.data

import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyBlocking
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import ru.olegivo.afs.BaseTestOf
import ru.olegivo.afs.common.CoroutineToRxAdapter
import ru.olegivo.afs.common.add
import ru.olegivo.afs.common.domain.DateProvider
import ru.olegivo.afs.common.firstDayOfWeek
import ru.olegivo.afs.helpers.capture
import ru.olegivo.afs.helpers.getRandomBoolean
import ru.olegivo.afs.helpers.getRandomInt
import ru.olegivo.afs.helpers.getRandomLong
import ru.olegivo.afs.helpers.givenBlocking
import ru.olegivo.afs.helpers.willReturn
import ru.olegivo.afs.schedule.data.ReserveNetworkSource
import ru.olegivo.afs.schedules.data.models.DataSchedule
import ru.olegivo.afs.schedules.data.models.createDataSchedule
import ru.olegivo.afs.schedules.data.models.toDomain
import ru.olegivo.afs.schedules.domain.ScheduleRepository
import ru.olegivo.afs.schedules.domain.models.createSchedule
import java.util.Date

class ScheduleRepositoryImplTest : BaseTestOf<ScheduleRepository>() {

    override fun createInstance() = ScheduleRepositoryImpl(
        scheduleNetworkSource = scheduleNetworkSource,
        scheduleDbSource = scheduleDbSource,
        dateProvider = dateProvider,
        coroutineToRxAdapter = CoroutineToRxAdapter().apply {
            coroutineContext = testDispatcher
        },
        computationScheduler = testScheduler
    )

    //<editor-fold desc="mocks">
    private val scheduleNetworkSource: ScheduleNetworkSource = mock()
    private val reserveNetworkSource: ReserveNetworkSource = mock()
    private val scheduleDbSource: ScheduleDbSource = mock()
    private val dateProvider: DateProvider = mock()

    override fun getAllMocks() = arrayOf(
        scheduleNetworkSource,
        reserveNetworkSource,
        scheduleDbSource,
        dateProvider
    )
    //</editor-fold>

    @Test
    fun `getCurrentWeekSchedule RETURNS transformed data from db source WHEN has data in db`() {
        val clubId = getRandomInt()
        val schedules = listOf(createDataSchedule())

        val now = Date()
        given(dateProvider.getDate()).willReturn(now)
        val weekStart = firstDayOfWeek(now)
        val nextWeekStart = weekStart.add(days = 7)

        given(scheduleDbSource.getSchedules(clubId, weekStart, nextWeekStart))
            .willReturn(Maybe.just(schedules))

        val result = instance.getCurrentWeekSchedule(clubId)
            .test().andTriggerActions()
            .assertNoErrors()
            .values().single()

        assertThat(result).extracting<String> { it.activity }
            .isEqualTo(schedules.map { it.activity })

        verify(dateProvider).getDate()
        verify(scheduleDbSource).getSchedules(clubId, weekStart, nextWeekStart)
    }

    @Test
    fun `setScheduleReserved CALL scheduleDbSource`() {
        val schedule = createSchedule()
        given(scheduleDbSource.setScheduleReserved(schedule)).willReturn(Completable.complete())

        instance.setScheduleReserved(schedule)
            .test().andTriggerActions()

        verify(scheduleDbSource).setScheduleReserved(schedule)
    }

    @Test
    fun `getCurrentWeekReservedScheduleIds RETURNS ids from scheduleDbSource`() {
        val now = Date()
        given(dateProvider.getDate()).willReturn(now)
        val weekStart = firstDayOfWeek(now)
        val nextWeekStart = weekStart.add(days = 7)
        val expectedIds = listOf(getRandomLong(), getRandomLong())
        given(scheduleDbSource.getReservedScheduleIds(weekStart, nextWeekStart))
            .willReturn(Single.just(expectedIds))

        val ids = instance.getCurrentWeekReservedScheduleIds()
            .test().andTriggerActions()
            .assertNoErrors()
            .assertComplete()
            .values()
            .single()

        assertThat(ids).containsExactlyElementsOf(expectedIds)
        verify(dateProvider).getDate()
        verify(scheduleDbSource).getReservedScheduleIds(weekStart, nextWeekStart)
    }

    @Test
    fun `actualizeSchedules SYNCS schedule from network to db`() {
        val clubId = getRandomInt()
        val schedules = listOf(createDataSchedule())

        givenBlocking(scheduleNetworkSource) { getSchedule(clubId) }
            .willReturn { schedules }
        given(scheduleDbSource.putSchedules(schedules)).willReturn(Completable.complete())

        instance.actualizeSchedules(clubId)
            .test().andTriggerActions()
            .assertNoErrors()
            .assertComplete()

        verifyBlocking(scheduleNetworkSource) { getSchedule(clubId) }
        val actual = scheduleDbSource.capture { p: List<DataSchedule> -> putSchedules(p) }
        assertThat(actual).isEqualTo(schedules)
    }

    @Test
    fun `getSchedule RETURNS data from dbSource`() {
        val dataSchedule = createDataSchedule()
        val schedule = dataSchedule.toDomain()
        given(scheduleDbSource.getSchedule(schedule.id))
            .willReturn(Single.just(dataSchedule))

        val result = instance.getSchedule(schedule.id)
            .test().andTriggerActions()
            .values().single()

        verify(scheduleDbSource).getSchedule(schedule.id)
        assertThat(result).isEqualTo(schedule)
    }

    @Test
    fun `isScheduleReserved RETURNS data from dbSource`() {
        val scheduleId = getRandomLong()
        val isScheduleReserved = getRandomBoolean()
        given(scheduleDbSource.isScheduleReserved(scheduleId))
            .willReturn(Single.just(isScheduleReserved))

        val result = instance.isScheduleReserved(scheduleId)
            .test().andTriggerActions()
            .assertNoErrors()
            .values().single()

        assertThat(result).isEqualTo(isScheduleReserved)
        verify(scheduleDbSource).isScheduleReserved(scheduleId)
    }
}
