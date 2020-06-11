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

package ru.olegivo.afs.schedules.domain

import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import ru.olegivo.afs.BaseTestOf
import ru.olegivo.afs.favorites.domain.FavoritesRepository
import ru.olegivo.afs.favorites.domain.models.toFavoriteFilter
import ru.olegivo.afs.randomSubList
import ru.olegivo.afs.repeat
import ru.olegivo.afs.schedules.domain.models.Schedule
import ru.olegivo.afs.schedules.domain.models.createSchedule
import ru.olegivo.afs.schedules.domain.models.createSlot
import kotlin.random.Random

class GetCurrentWeekSportsActivitiesUseCaseImplTest : BaseTestOf<GetCurrentWeekScheduleUseCase>() {

    override fun createInstance() = GetCurrentWeekSportsActivitiesUseCaseImpl(
        scheduleRepository,
        actualizeScheduleUseCase,
        favoritesRepository,
        schedulerRule.testScheduler
    )

    //<editor-fold desc="mocks">
    private val scheduleRepository: ScheduleRepository = mock()
    private val favoritesRepository: FavoritesRepository = mock()
    private val actualizeScheduleUseCase: ActualizeScheduleUseCase = mock()

    override fun getAllMocks() = arrayOf(
        scheduleRepository,
        favoritesRepository
    )
    //</editor-fold>

    @Test
    fun `invoke RETURNS data from repository WHEN has data`() {
        val schedules = { createSchedule() }.repeat(20)
        val clubId = Random.nextInt()
        given(scheduleRepository.getCurrentWeekSchedule(clubId)).willReturn(Maybe.just(schedules))
        val ids = schedules.map { it.id }
        val slots = ids.map(::createSlot)
        given(scheduleRepository.getSlots(clubId, ids)).willReturn(Single.just(slots))
        val reservedScheduleIds = ids.randomSubList()
        given(scheduleRepository.getCurrentWeekReservedScheduleIds())
            .willReturn(Single.just(reservedScheduleIds))
        val favoriteSchedules = schedules.randomSubList()
        val favoriteScheduleIds = favoriteSchedules.map { it.id }
        val favoriteFilters = favoriteSchedules.map { schedule ->
            schedule.toFavoriteFilter()
        }
        given(favoritesRepository.getFavoriteFilters())
            .willReturn(Single.just(favoriteFilters))

        instance.invoke(clubId)
            .assertResult { sportsActivities ->
                assertThat(sportsActivities.map { it.schedule }).isEqualTo(schedules)
                assertThat(sportsActivities.filter { it.isReserved }.map { it.schedule.id })
                    .isEqualTo(reservedScheduleIds)
                assertThat(sportsActivities.filter { it.isFavorite }.map { it.schedule.id })
                    .isEqualTo(favoriteScheduleIds)
            }

        verify(scheduleRepository).getCurrentWeekSchedule(clubId)
        verify(scheduleRepository).getSlots(clubId, ids)
        verify(scheduleRepository).getCurrentWeekReservedScheduleIds()
        verify(favoritesRepository).getFavoriteFilters()
    }

    @Test
    fun `invoke ACTUALIZES and returns data from repository WHEN has no data before`() {
        val schedules = { createSchedule() }.repeat(20)
        val clubId = Random.nextInt()
        val empty = Maybe.empty<List<Schedule>>()
            .doOnComplete {
                given(actualizeScheduleUseCase(clubId))
                    .willReturn(Completable.complete())
                given(scheduleRepository.getCurrentWeekSchedule(clubId))
                    .willReturn(Maybe.just(schedules))
            }
        given(scheduleRepository.getCurrentWeekSchedule(clubId))
            .willReturn(empty)
        val ids = schedules.map { it.id }
        val slots = ids.map(::createSlot)
        given(scheduleRepository.getSlots(clubId, ids)).willReturn(Single.just(slots))
        val reservedScheduleIds = ids.randomSubList()
        given(scheduleRepository.getCurrentWeekReservedScheduleIds())
            .willReturn(Single.just(reservedScheduleIds))
        val favoriteSchedules = schedules.randomSubList()
        val favoriteScheduleIds = favoriteSchedules.map { it.id }
        val favoriteFilters = favoriteSchedules.map { schedule ->
            schedule.toFavoriteFilter()
        }
        given(favoritesRepository.getFavoriteFilters())
            .willReturn(Single.just(favoriteFilters))

        instance.invoke(clubId)
            .assertResult { sportsActivities ->
                assertThat(sportsActivities.map { it.schedule }).isEqualTo(schedules)
                assertThat(sportsActivities.filter { it.isReserved }.map { it.schedule.id })
                    .isEqualTo(reservedScheduleIds)
                assertThat(sportsActivities.filter { it.isFavorite }.map { it.schedule.id })
                    .isEqualTo(favoriteScheduleIds)
            }

        verify(scheduleRepository, times(2)).getCurrentWeekSchedule(clubId)
        verify(actualizeScheduleUseCase)(clubId)
        verify(scheduleRepository).getSlots(clubId, ids)
        verify(scheduleRepository).getCurrentWeekReservedScheduleIds()
        verify(favoritesRepository).getFavoriteFilters()
    }
}
