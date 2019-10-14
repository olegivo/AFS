package ru.olegivo.afs.schedule.data

import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.Single
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import ru.olegivo.afs.BaseTest
import ru.olegivo.afs.helpers.getRandomInt
import ru.olegivo.afs.reserve.data.ReserveNetworkSource
import ru.olegivo.afs.schedule.data.models.createDataSchedule
import ru.olegivo.afs.schedule.data.models.createSlot
import ru.olegivo.afs.schedule.domain.ScheduleRepository

class ScheduleRepositoryImplTest : BaseTest() {
    private val scheduleNetworkSource: ScheduleNetworkSource = mock()
    private val reserveNetworkSource: ReserveNetworkSource = mock()

    private val scheduleRepository: ScheduleRepository = ScheduleRepositoryImpl(
        scheduleNetworkSource,
        reserveNetworkSource
    )

    override fun getAllMocks() = arrayOf(
        scheduleNetworkSource,
        reserveNetworkSource
    )

    @Test
    fun `getCurrentWeekSchedule returns transformed data from network source`() {
        val clubId = getRandomInt()
        val schedules = listOf(createDataSchedule())
        val ids = schedules.map { it.id }
        val slots = ids.map(::createSlot)

        given(scheduleNetworkSource.getSchedule(clubId)).willReturn(Single.just(schedules))
        given(reserveNetworkSource.getSlots(clubId, ids)).willReturn(Single.just(slots))

        val result = scheduleRepository.getCurrentWeekSchedule(clubId)
            .test().andTriggerActions()
            .assertNoErrors()
            .values().single()

        assertThat(result).extracting<String> {it.activity }.isEqualTo(schedules.map { it.activity })

        verify(scheduleNetworkSource).getSchedule(clubId)
        verify(reserveNetworkSource).getSlots(clubId, ids)
    }

}
