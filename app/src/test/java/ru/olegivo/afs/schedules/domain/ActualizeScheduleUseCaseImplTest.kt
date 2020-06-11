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
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.willReturn
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.Test
import ru.olegivo.afs.BaseTestOf
import ru.olegivo.afs.favorites.domain.FavoritesRepository
import ru.olegivo.afs.favorites.domain.PlanFavoriteRecordReminderUseCase
import ru.olegivo.afs.favorites.domain.models.toFavoriteFilter
import ru.olegivo.afs.helpers.getRandomInt
import ru.olegivo.afs.randomSubList
import ru.olegivo.afs.schedules.domain.models.createSchedule

class ActualizeScheduleUseCaseImplTest : BaseTestOf<ActualizeScheduleUseCase>() {

    override fun createInstance(): ActualizeScheduleUseCase = ActualizeScheduleUseCaseImpl(
        scheduleRepository,
        favoritesRepository,
        planFavoriteRecordReminderUseCase,
        schedulerRule.testScheduler
    )

    //<editor-fold desc="mocks">
    private val scheduleRepository: ScheduleRepository = mock()
    private val favoritesRepository: FavoritesRepository = mock()
    private val planFavoriteRecordReminderUseCase: PlanFavoriteRecordReminderUseCase = mock()

    override fun getAllMocks() = arrayOf(
        scheduleRepository,
        favoritesRepository,
        planFavoriteRecordReminderUseCase
    )
    //</editor-fold>

    @Test
    fun `invoke CALLS scheduleRepository`() {
        val clubId = getRandomInt()
        val schedules = listOf(createSchedule())
        given(scheduleRepository.actualizeSchedules(clubId))
            .willReturn { Single.just(schedules) }

        val favoriteSchedules = schedules.randomSubList()
        val favoriteFilters = favoriteSchedules.map { it.toFavoriteFilter() }
        favoriteSchedules.forEach {
            given(planFavoriteRecordReminderUseCase.invoke(it)).willReturn { Completable.complete() }
        }

        given(favoritesRepository.getFavoriteFilters()).willReturn { Single.just(favoriteFilters) }

        instance.invoke(clubId)
            .assertSuccess()

        verify(scheduleRepository).actualizeSchedules(clubId)
        verify(favoritesRepository).getFavoriteFilters()
        favoriteSchedules.forEach {
            verify(planFavoriteRecordReminderUseCase).invoke(it)
        }
    }
}
