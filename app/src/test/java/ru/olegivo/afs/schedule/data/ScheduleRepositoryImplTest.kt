package ru.olegivo.afs.schedule.data

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
import ru.olegivo.afs.reserve.data.ReserveNetworkSource
import ru.olegivo.afs.schedule.data.models.createDataSchedule
import ru.olegivo.afs.schedule.data.models.createSlot
import ru.olegivo.afs.schedule.domain.ScheduleRepository
import ru.olegivo.afs.schedule.domain.models.createSchedule
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
        val ids = schedules.map { it.id }
        val slots = ids.map(::createSlot)

        given(scheduleNetworkSource.getSchedule(clubId)).willReturn(Single.just(schedules))
        given(scheduleNetworkSource.getSlots(clubId, ids)).willReturn(Single.just(slots))
        val now = Date()
        given(dateProvider.getDate()).willReturn(now)
        val weekStart = firstDayOfWeek(now)
        val nextWeekStart = weekStart.add(days = 7)
        given(scheduleDbSource.getReservedScheduleIds(weekStart, nextWeekStart))
            .willReturn(Single.just(listOf()))

        val result = instance.getCurrentWeekSchedule(clubId)
            .test().andTriggerActions()
            .assertNoErrors()
            .values().single()

        assertThat(result).extracting<String> { it.activity }
            .isEqualTo(schedules.map { it.activity })

        verify(scheduleNetworkSource).getSchedule(clubId)
        verify(scheduleNetworkSource).getSlots(clubId, ids)
        verify(dateProvider).getDate()
        verify(scheduleDbSource).getReservedScheduleIds(weekStart, nextWeekStart)
    }

    @Test
    fun `setScheduleReserved CALL scheduleDbSource`() {
        val schedule = createSchedule()
        given(scheduleDbSource.setScheduleReserved(schedule)).willReturn(Completable.complete())

        instance.setScheduleReserved(schedule)
            .test().andTriggerActions()

        verify(scheduleDbSource).setScheduleReserved(schedule)
    }

}
