package ru.olegivo.afs.schedule.data

import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.Single
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import ru.olegivo.afs.BaseTest
import ru.olegivo.afs.helpers.getRandomInt
import ru.olegivo.afs.schedule.domain.ScheduleRepository
import ru.olegivo.afs.schedule.domain.models.createSchedule

class ScheduleRepositoryImplTest : BaseTest() {
    private val scheduleNetworkSource: ScheduleNetworkSource = mock()

    private val scheduleRepository: ScheduleRepository = ScheduleRepositoryImpl(
        scheduleNetworkSource
    )

    override fun getAllMocks() = arrayOf<Any>(
        scheduleNetworkSource
    )

    @Test
    fun `getCurrentWeekSchedule returns transformed data from network source`() {
        val clubId = getRandomInt()
        val schedules = listOf(createSchedule())

        given(scheduleNetworkSource.getSchedule(clubId)).willReturn(Single.just(schedules))

        val result = scheduleRepository.getCurrentWeekSchedule(clubId)
            .test().andTriggerActions()
            .assertNoErrors()
            .values().single()

        assertThat(result).isEqualTo(schedules)

        verify(scheduleNetworkSource).getSchedule(clubId)
    }

}
