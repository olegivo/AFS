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
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import ru.olegivo.afs.BaseTestOf
import ru.olegivo.afs.common.add
import ru.olegivo.afs.common.date
import ru.olegivo.afs.common.db.AfsDatabase
import ru.olegivo.afs.common.domain.DateProvider
import ru.olegivo.afs.extensions.toSingle
import ru.olegivo.afs.favorites.data.models.createFavoriteFilter
import ru.olegivo.afs.favorites.domain.models.FavoriteFilter
import ru.olegivo.afs.helpers.checkSingleValue
import ru.olegivo.afs.helpers.getRandomDate
import ru.olegivo.afs.helpers.getRandomInt
import ru.olegivo.afs.schedules.data.models.createDataSchedule
import ru.olegivo.afs.schedules.db.ScheduleDao
import ru.olegivo.afs.schedules.db.models.ScheduleEntity
import ru.olegivo.afs.schedules.db.models.toDb
import java.util.Calendar
import java.util.Date

class GetClosestSportsActivityUseCaseImplTest : BaseTestOf<GetClosestSportsActivityUseCase>() {

    override fun createInstance() = GetClosestSportsActivityUseCaseImpl(
        afsDatabase = afsDatabase,
        dateProvider = dateProvider,
        ioScheduler = testScheduler
    )

    //<editor-fold desc="Mocks">
    override fun getAllMocks(): Array<Any> = arrayOf(
        afsDatabase,
        dateProvider
    )

    private val afsDatabase: AfsDatabase = mock()
    private val dateProvider: DateProvider = mock()
    private val scheduleDao: ScheduleDao = mock()
    //</editor-fold>

    @Test
    fun `invoke RETURNS empty WHEN has no schedules in Db`() {
        val filter = createFavoriteFilter()

        setup(filter, emptyList())

        instance.invoke(filter, filter.clubId)
            .test().andTriggerActions()
            .assertNoErrors()
            .assertComplete()
            .assertNoValues()

        verify(filter)
    }

    @Test
    fun `invoke RETURNS empty WHEN has no schedules with that dayOfWeek`() {
        val filter = createFavoriteFilter().copy(
            dayOfWeek = Calendar.FRIDAY,
            minutesOfDay = 60 * 21
        )
        // thursday, 21:00
        val now = date(2020, 10, 19, 21, 0)

        val element = createDataSchedule().toDb().copy(
            clubId = filter.clubId,
            groupId = filter.groupId,
            group = filter.group,
            activityId = filter.activityId,
            activity = filter.activity,
            datetime = now
        )

        setup(filter, listOf(element), now)

        instance.invoke(filter, filter.clubId)
            .test().andTriggerActions()
            .assertNoErrors()
            .assertComplete()
            .assertNoValues()

        verify(filter)
    }

    @Test
    fun `invoke RETURNS empty WHEN has no schedules with that minutesOfDay`() {
        val filter = createFavoriteFilter().copy(
            dayOfWeek = Calendar.FRIDAY,
            minutesOfDay = 60 * 21
        )
        // friday, 20:00
        val now = date(2020, 10, 20, 20, 0)

        val element = createDataSchedule().toDb().copy(
            clubId = filter.clubId,
            groupId = filter.groupId,
            group = filter.group,
            activityId = filter.activityId,
            activity = filter.activity,
            datetime = now
        )

        setup(filter, listOf(element), now)

        instance.invoke(filter, filter.clubId)
            .test().andTriggerActions()
            .assertNoErrors()
            .assertComplete()
            .assertNoValues()

        verify(filter)
    }

    @Test
    fun `invoke RETURNS the one id WHEN has single schedule with those dayOfWeek and minutesOfDay`() {
        val filter = createFavoriteFilter().copy(
            dayOfWeek = Calendar.FRIDAY,
            minutesOfDay = 60 * 21
        )
        // friday, 21:00
        val now = date(2020, 10, 20, 21, 0)

        val element = createDataSchedule().toDb().copy(
            clubId = filter.clubId,
            groupId = filter.groupId,
            group = filter.group,
            activityId = filter.activityId,
            activity = filter.activity,
            datetime = now
        )

        setup(filter, listOf(element), now)

        instance.invoke(filter, filter.clubId)
            .test().andTriggerActions()
            .assertNoErrors()
            .checkSingleValue {
                assertThat(it).isEqualTo(element.id)
            }

        verify(filter)
    }

    @Test
    fun `invoke RETURNS id from the middle WHEN has 3 schedules - 1 week later, 1 weeks earlier, 1 in the middle of them`() {
        val filter = createFavoriteFilter().copy(
            dayOfWeek = Calendar.FRIDAY,
            minutesOfDay = 60 * 21
        )
        // friday, 21:00
        val now = date(2020, 10, 20, 21, 0)
        val template = createDataSchedule().toDb().copy(
            clubId = filter.clubId,
            groupId = filter.groupId,
            group = filter.group,
            activityId = filter.activityId,
            activity = filter.activity
        )

        val past = template.copy(
            datetime = now.add(days = -7)
        )
        val middle = template.copy(
            datetime = now.add(days = getRandomInt(from = -6, until = 7))
        )
        val future = template.copy(
            datetime = now.add(days = 7)
        )

        setup(
            filter = filter,
            list = listOf(past, middle, future),
            now = now
        )

        instance.invoke(filter, filter.clubId)
            .test().andTriggerActions()
            .assertNoErrors()
            .checkSingleValue {
                assertThat(it).isEqualTo(middle.id)
            }

        verify(filter)
    }

    @Test
    fun `invoke RETURNS id from the earlier WHEN has 2 schedules - 1 week later, 2 weeks later`() {
        val filter = createFavoriteFilter().copy(
            dayOfWeek = Calendar.FRIDAY,
            minutesOfDay = 60 * 21
        )
        // friday, 21:00
        val now = date(2020, 10, 20, 21, 0)
        val template = createDataSchedule().toDb().copy(
            clubId = filter.clubId,
            groupId = filter.groupId,
            group = filter.group,
            activityId = filter.activityId,
            activity = filter.activity
        )

        val earlier = template.copy(
            datetime = now.add(days = 7)
        )
        val later = template.copy(
            datetime = now.add(days = 14)
        )

        setup(
            filter = filter,
            list = listOf(earlier, later),
            now = now
        )

        instance.invoke(filter, filter.clubId)
            .test().andTriggerActions()
            .assertNoErrors()
            .checkSingleValue {
                assertThat(it).isEqualTo(earlier.id)
            }

        verify(filter)
    }

    @Test
    fun `invoke RETURNS id from the later WHEN has 2 schedules - 1 week eralier, 2 weeks earlier`() {
        val filter = createFavoriteFilter().copy(
            dayOfWeek = Calendar.FRIDAY,
            minutesOfDay = 60 * 21
        )
        // friday, 21:00
        val now = date(2020, 10, 20, 21, 0)
        val template = createDataSchedule().toDb().copy(
            clubId = filter.clubId,
            groupId = filter.groupId,
            group = filter.group,
            activityId = filter.activityId,
            activity = filter.activity
        )

        val earlier = template.copy(
            datetime = now.add(days = -14)
        )
        val later = template.copy(
            datetime = now.add(days = -7)
        )

        setup(
            filter = filter,
            list = listOf(earlier, later),
            now = now
        )

        instance.invoke(filter, filter.clubId)
            .test().andTriggerActions()
            .assertNoErrors()
            .checkSingleValue {
                assertThat(it).isEqualTo(later.id)
            }

        verify(filter)
    }

    private fun setup(
        filter: FavoriteFilter,
        list: List<ScheduleEntity>,
        now: Date = getRandomDate()
    ) {
        given { afsDatabase.schedules }.willReturn(scheduleDao)
        given { scheduleDao.filterSchedules(filter, filter.clubId) }
            .willReturn(list.toSingle())
        given { dateProvider.getDate() }.willReturn(now)
    }

    private fun verify(filter: FavoriteFilter) {
        verify(afsDatabase).schedules
        verify(scheduleDao).filterSchedules(filter, filter.clubId)
        verify(dateProvider).getDate()
    }
}
