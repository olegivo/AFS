package ru.olegivo.afs.schedules.data

import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.Completable
import io.reactivex.Single
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import ru.olegivo.afs.BaseTestOf
import ru.olegivo.afs.common.add
import ru.olegivo.afs.common.domain.DateProvider
import ru.olegivo.afs.common.firstDayOfWeek
import ru.olegivo.afs.helpers.getRandomInt
import ru.olegivo.afs.helpers.getRandomLong
import ru.olegivo.afs.schedule.data.ReserveNetworkSource
import ru.olegivo.afs.schedules.data.models.createDataSchedule
import ru.olegivo.afs.schedules.domain.ScheduleRepository
import ru.olegivo.afs.schedules.domain.models.createSchedule
import java.util.*

class ScheduleRepositoryImplTest : BaseTestOf<ScheduleRepository>() {

    override fun createInstance() = ScheduleRepositoryImpl(
        scheduleNetworkSource,
        scheduleDbSource,
        dateProvider,
        schedulerRule.testScheduler
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
    fun `getCurrentWeekSchedule returns transformed data from network source`() {
        val clubId = getRandomInt()
        val schedules = listOf(createDataSchedule())

        given(scheduleNetworkSource.getSchedule(clubId)).willReturn(Single.just(schedules))

        val result = instance.getCurrentWeekSchedule(clubId)
            .test().andTriggerActions()
            .assertNoErrors()
            .values().single()

        assertThat(result).extracting<String> { it.activity }
            .isEqualTo(schedules.map { it.activity })

        verify(scheduleNetworkSource).getSchedule(clubId)
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
}