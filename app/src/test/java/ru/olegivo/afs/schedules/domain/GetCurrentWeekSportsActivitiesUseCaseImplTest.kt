package ru.olegivo.afs.schedules.domain

import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.Single
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import ru.olegivo.afs.BaseTest
import ru.olegivo.afs.schedules.domain.models.createSchedule
import ru.olegivo.afs.schedules.domain.models.createSlot
import kotlin.random.Random

class GetCurrentWeekSportsActivitiesUseCaseImplTest : BaseTest() {
    private val scheduleRepository: ScheduleRepository = mock()

    private val getCurrentWeekScheduleUseCase: GetCurrentWeekScheduleUseCase =
        GetCurrentWeekSportsActivitiesUseCaseImpl(
            scheduleRepository
        )

    override fun getAllMocks() = arrayOf<Any>(
        scheduleRepository
    )

    @Test
    fun `invoke returns data from repository`() {
        val schedules = listOf(createSchedule(), createSchedule())
        val clubId = Random.nextInt()
        given(scheduleRepository.getCurrentWeekSchedule(clubId)).willReturn(Single.just(schedules))
        val ids = schedules.map { it.id }
        val slots = ids.map(::createSlot)
        given(scheduleRepository.getSlots(clubId, ids)).willReturn(Single.just(slots))
        val reservedScheduleIds = ids.take(1)
        given(scheduleRepository.getCurrentWeekReservedScheduleIds())
            .willReturn(Single.just(reservedScheduleIds))

        getCurrentWeekScheduleUseCase.invoke(clubId)
            .assertResult { sportsActivities ->
                assertThat(sportsActivities.map { it.schedule }).isEqualTo(schedules)
                assertThat(sportsActivities.filter { it.isReserved }.map { it.schedule.id })
                    .isEqualTo(reservedScheduleIds)
            }

        verify(scheduleRepository).getCurrentWeekSchedule(clubId)
        verify(scheduleRepository).getSlots(clubId, ids)
        verify(scheduleRepository).getCurrentWeekReservedScheduleIds()
    }

}