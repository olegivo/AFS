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

package ru.olegivo.afs.schedule.domain

import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.Single
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import ru.olegivo.afs.BaseTestOf
import ru.olegivo.afs.favorites.domain.FavoritesRepository
import ru.olegivo.afs.helpers.getRandomBoolean
import ru.olegivo.afs.helpers.getRandomInt
import ru.olegivo.afs.helpers.getRandomLong
import ru.olegivo.afs.schedules.domain.ScheduleRepository
import ru.olegivo.afs.schedules.domain.models.Schedule
import ru.olegivo.afs.schedules.domain.models.Slot
import ru.olegivo.afs.schedules.domain.models.SportsActivity
import ru.olegivo.afs.schedules.domain.models.createSchedule
import ru.olegivo.afs.schedules.domain.models.createSlot

class GetSportsActivityUseCaseImplTest : BaseTestOf<GetSportsActivityUseCase>() {

    override fun createInstance() = GetSportsActivityUseCaseImpl(
        scheduleRepository,
        favoritesRepository
    )

    //<editor-fold desc="mocks">
    private val scheduleRepository: ScheduleRepository = mock()
    private val favoritesRepository: FavoritesRepository = mock()

    override fun getAllMocks(): Array<Any> = arrayOf(
        scheduleRepository,
        favoritesRepository
    )
    //</editor-fold>

    @Test
    fun `invoke RETURNS sports activity from repository WHEN available slots present`() {
        val clubId = getRandomInt()
        val scheduleId = getRandomLong()
        val schedule = createSchedule()
        val slot = createSlot(scheduleId)
        val isReserved = getRandomBoolean()
        val isFavorite = getRandomBoolean()

        setupResponse(scheduleId, schedule, clubId, slot, isReserved, isFavorite)

        val result = instance.invoke(clubId, scheduleId)
            .test().andTriggerActions()
            .assertNoErrors()
            .values().single()

        checkResult(scheduleId, clubId, schedule, result, slot, isReserved, isFavorite)
    }

    @Test
    fun `invoke RETURNS sports activity from repository WHEN available slots not present`() {
        val clubId = getRandomInt()
        val scheduleId = getRandomLong()
        val schedule = createSchedule()
        val slot = createSlot(scheduleId).copy(slots = null)
        val isReserved = getRandomBoolean()
        val isFavorite = getRandomBoolean()

        setupResponse(scheduleId, schedule, clubId, slot, isReserved, isFavorite)

        val result = instance.invoke(clubId, scheduleId)
            .test().andTriggerActions()
            .assertNoErrors()
            .values().single()

        checkResult(scheduleId, clubId, schedule, result, slot, isReserved, isFavorite)
    }

    @Test
    fun `invoke RETURNS sports activity from repository WHEN wrong slots present`() {
        val clubId = getRandomInt()
        val scheduleId = getRandomLong()
        val schedule = createSchedule()
        val slot = createSlot(scheduleId + 1)
        val isReserved = getRandomBoolean()
        val isFavorite = getRandomBoolean()

        setupResponse(scheduleId, schedule, clubId, slot, isReserved, isFavorite)

        val result = instance.invoke(clubId, scheduleId)
            .test().andTriggerActions()
            .assertNoErrors()
            .values().single()

        checkResult(scheduleId, clubId, schedule, result, null, isReserved, isFavorite)
    }

    private fun setupResponse(
        scheduleId: Long,
        schedule: Schedule,
        clubId: Int,
        slot: Slot,
        isReserved: Boolean,
        isFavorite: Boolean
    ) {
        given(scheduleRepository.getSchedule(scheduleId))
            .willReturn(Single.just(schedule))
        given(scheduleRepository.getSlots(clubId, listOf(scheduleId)))
            .willReturn(Single.just(listOf(slot)))
        given(scheduleRepository.isScheduleReserved(scheduleId))
            .willReturn(Single.just(isReserved))
        given(favoritesRepository.isFavorite(schedule))
            .willReturn(Single.just(isFavorite))
    }

    private fun checkResult(
        scheduleId: Long,
        clubId: Int,
        schedule: Schedule,
        result: SportsActivity,
        slot: Slot?,
        isReserved: Boolean,
        isFavorite: Boolean
    ) {
        verify(scheduleRepository).getSchedule(scheduleId)
        verify(scheduleRepository).getSlots(clubId, listOf(scheduleId))
        verify(scheduleRepository).isScheduleReserved(scheduleId)
        verify(favoritesRepository).isFavorite(schedule)

        assertThat(result.schedule).isEqualTo(schedule)
        assertThat(result.availableSlots).isEqualTo(slot?.slots)
        assertThat(result.isReserved).isEqualTo(isReserved)
        assertThat(result.isFavorite).isEqualTo(isFavorite)
    }
}
