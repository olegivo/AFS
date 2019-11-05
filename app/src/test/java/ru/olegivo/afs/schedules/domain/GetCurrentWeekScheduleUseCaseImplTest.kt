package ru.olegivo.afs.schedules.domain

import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.Single
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import ru.olegivo.afs.BaseTest
import ru.olegivo.afs.schedules.domain.models.createSchedule
import kotlin.random.Random

class GetCurrentWeekScheduleUseCaseImplTest : BaseTest() {
    private val scheduleRepository: ScheduleRepository = mock()

    private val getCurrentWeekScheduleUseCase: GetCurrentWeekScheduleUseCase =
        GetCurrentWeekScheduleUseCaseImpl(
            scheduleRepository
        )

    override fun getAllMocks() = arrayOf<Any>(
        scheduleRepository
    )

    @Test
    fun `invoke returns data from repository`() {
        val list = listOf(createSchedule())
        val clubId = Random.nextInt()
        given(scheduleRepository.getCurrentWeekSchedule(clubId)).willReturn(Single.just(list))

        getCurrentWeekScheduleUseCase.invoke(clubId)
            .assertResult {
                assertThat(it).isEqualTo(list)
            }

        verify(scheduleRepository).getCurrentWeekSchedule(clubId)
    }

}