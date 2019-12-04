package ru.olegivo.afs.schedules.domain

import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.Single
import org.junit.Test
import ru.olegivo.afs.BaseTestOf
import ru.olegivo.afs.helpers.getRandomInt
import ru.olegivo.afs.schedules.domain.models.createSchedule

class ActualizeScheduleUseCaseImplTest : BaseTestOf<ActualizeScheduleUseCase>() {

    override fun createInstance(): ActualizeScheduleUseCase = ActualizeScheduleUseCaseImpl(
        scheduleRepository
    )

    //<editor-fold desc="mocks">
    private val scheduleRepository: ScheduleRepository = mock()

    override fun getAllMocks() = arrayOf<Any>(scheduleRepository)
    //</editor-fold>

    @Test
    fun `invoke CALLS scheduleRepository`() {
        val clubId = getRandomInt()
        val schedules = listOf(createSchedule())
        given(scheduleRepository.actualizeSchedules(clubId))
            .willReturn(Single.just(schedules))

        instance.invoke(clubId)
            .test().andTriggerActions()
            .assertNoErrors()
            .assertComplete()

        verify(scheduleRepository).actualizeSchedules(clubId)
    }
}