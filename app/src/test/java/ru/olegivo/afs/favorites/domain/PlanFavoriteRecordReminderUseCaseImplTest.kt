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

package ru.olegivo.afs.favorites.domain

import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.willReturn
import io.reactivex.Single
import org.junit.Test
import ru.olegivo.afs.BaseTestOf
import ru.olegivo.afs.common.add
import ru.olegivo.afs.common.domain.DateProvider
import ru.olegivo.afs.helpers.willComplete
import ru.olegivo.afs.schedules.domain.models.createSchedule
import java.util.Date

class PlanFavoriteRecordReminderUseCaseImplTest : BaseTestOf<PlanFavoriteRecordReminderUseCase>() {
    override fun createInstance() =
        PlanFavoriteRecordReminderUseCaseImpl(
            favoritesRepository,
            favoriteAlarmPlanner,
            dateProvider
        )

    private val favoritesRepository: FavoritesRepository = mock()
    private val favoriteAlarmPlanner: FavoriteAlarmPlanner = mock()
    private val dateProvider: DateProvider = mock()

    override fun getAllMocks() = arrayOf(
        favoritesRepository,
        favoriteAlarmPlanner,
        dateProvider
    )

    @Test
    fun `invoke PLANS reminder WHEN recordTo not passed, reminder was not planned`() {
        val recordTo = Date()
        val schedule = createSchedule().copy(recordTo = recordTo)
        val now = recordTo.add(hours = -1)

        given { dateProvider.getDate() }.willReturn { now }
        given {
            favoritesRepository.addReminderToRecord(
                schedule.id,
                schedule.recordFrom!!,
                schedule.recordTo!!
            )
        }.willComplete()
        given { favoriteAlarmPlanner.planFavoriteRecordReminder(schedule) }
            .willComplete()
        given { favoritesRepository.hasPlannedReminderToRecord(schedule) }
            .willReturn { Single.just(false) }

        instance.invoke(schedule)
            .assertSuccess()

        verify(dateProvider).getDate()
        verify(favoritesRepository).hasPlannedReminderToRecord(schedule)
        verify(favoritesRepository).addReminderToRecord(
            schedule.id,
            schedule.recordFrom!!,
            schedule.recordTo!!
        )
        verify(favoriteAlarmPlanner).planFavoriteRecordReminder(schedule)
    }

    @Test
    fun `invoke PLANS reminder WHEN recordTo not specified, start time not passed, reminder was not planned`() {
        val datetime = Date()
        val schedule = createSchedule().copy(recordTo = null, datetime = datetime)
        val now = datetime.add(hours = -1)

        given { dateProvider.getDate() }.willReturn { now }
        given {
            favoritesRepository.addReminderToRecord(
                schedule.id,
                schedule.getReminderDateFrom(),
                schedule.getReminderDateUntil()
            )
        }
            .willComplete()
        given { favoritesRepository.hasPlannedReminderToRecord(schedule) }
            .willReturn { Single.just(false) }
        given { favoriteAlarmPlanner.planFavoriteRecordReminder(schedule) }
            .willComplete()

        instance.invoke(schedule)
            .assertSuccess()

        verify(dateProvider).getDate()
        verify(favoritesRepository).hasPlannedReminderToRecord(schedule)
        verify(favoritesRepository).addReminderToRecord(
            schedule.id,
            schedule.getReminderDateFrom(),
            schedule.getReminderDateUntil()
        )
        verify(favoriteAlarmPlanner).planFavoriteRecordReminder(schedule)
    }

    @Test
    fun `invoke PLANS reminder 3 hours before the start date day WHEN recordFrom and recordFrom not specified, recordTo not specified, start time not passed (now is the day before), reminder was not planned`() {
        val datetime = Date()
        val schedule =
            createSchedule().copy(recordFrom = null, recordTo = null, datetime = datetime)
        val now = datetime.add(days = -1)

        given { dateProvider.getDate() }.willReturn { now }
        given { favoritesRepository.hasPlannedReminderToRecord(schedule) }
            .willReturn { Single.just(false) }
        given { favoriteAlarmPlanner.planFavoriteRecordReminder(schedule) }
            .willComplete()
        given {
            favoritesRepository.addReminderToRecord(
                schedule.id,
                schedule.getReminderDateFrom(),
                schedule.getReminderDateUntil()
            )
        }.willComplete()

        instance.invoke(schedule)
            .assertSuccess()

        verify(dateProvider).getDate()
        verify(favoritesRepository).hasPlannedReminderToRecord(schedule)
        verify(favoritesRepository).addReminderToRecord(
            schedule.id,
            schedule.getReminderDateFrom(),
            schedule.getReminderDateUntil()
        )
        verify(favoriteAlarmPlanner).planFavoriteRecordReminder(schedule)
    }

    @Test
    fun `invoke DOES nothing WHEN recordTo not specified, start time not passed, reminder already planned`() {
        val datetime = Date()
        val schedule = createSchedule().copy(recordTo = null, datetime = datetime)
        val now = datetime.add(hours = -1)

        given { dateProvider.getDate() }.willReturn { now }
        given { favoritesRepository.hasPlannedReminderToRecord(schedule) }
            .willReturn { Single.just(true) }

        instance.invoke(schedule)
            .assertSuccess()

        verify(dateProvider).getDate()
        verify(favoritesRepository).hasPlannedReminderToRecord(schedule)
    }

    @Test
    fun `invoke DOES nothing WHEN recordTo passed`() {
        val recordTo = Date()
        val schedule = createSchedule().copy(recordTo = recordTo)
        val now = recordTo.add(hours = 1)

        given { dateProvider.getDate() }.willReturn { now }

        instance.invoke(schedule)
            .assertSuccess()

        verify(dateProvider).getDate()
    }

    @Test
    fun `invoke DOES nothing WHEN recordTo not specified and start time passed`() {
        val datetime = Date()
        val schedule = createSchedule().copy(recordTo = null, datetime = datetime)
        val now = datetime.add(hours = 1)

        given { dateProvider.getDate() }.willReturn { now }

        instance.invoke(schedule)
            .assertSuccess()

        verify(dateProvider).getDate()
    }
}
