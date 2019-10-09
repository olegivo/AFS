package ru.olegivo.afs.schedule.presentation

import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.reset
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import io.reactivex.Maybe
import io.reactivex.Single
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import ru.olegivo.afs.BaseTest
import ru.olegivo.afs.clubs.domain.GetCurrentClubUseCase
import ru.olegivo.afs.common.add
import ru.olegivo.afs.common.domain.DateProvider
import ru.olegivo.afs.common.firstDayOfWeek
import ru.olegivo.afs.common.getDateWithoutTime
import ru.olegivo.afs.common.presentation.Navigator
import ru.olegivo.afs.helpers.capture
import ru.olegivo.afs.helpers.getRandomInt
import ru.olegivo.afs.reserve.presentation.models.ReserveDestination
import ru.olegivo.afs.schedule.domain.GetCurrentWeekScheduleUseCase
import ru.olegivo.afs.schedule.domain.models.Schedule
import ru.olegivo.afs.schedule.domain.models.createSchedule
import java.util.*


class WeekSchedulePresenterTest : BaseTest() {

    //<editor-fold desc="mocks">
    private val getCurrentClubUseCase: GetCurrentClubUseCase = mock()
    private val getCurrentWeekScheduleUseCase: GetCurrentWeekScheduleUseCase = mock()
    private val view: ScheduleContract.View = mock()
    private val dateProvider: DateProvider = mock()

    private val navigator: Navigator = mock()

    override fun getAllMocks() = arrayOf(
        getCurrentWeekScheduleUseCase,
        view,
        dateProvider,
        navigator
    )
    //</editor-fold>

    private val weekSchedulePresenter: ScheduleContract.Presenter = WeekSchedulePresenter(
        getCurrentClubUseCase,
        getCurrentWeekScheduleUseCase,
        dateProvider,
        navigator,
        schedulerRule.testScheduler
    )

    @Test
    fun `start shows current day schedule`() {
        val testData = TestData()
        setupGetCurrentWeekSchedule(testData)
        weekSchedulePresenter.bindView(view)

        weekSchedulePresenter.start()
            .andTriggerActions()

        val shownSchedules = view.capture { param: List<Schedule> -> showSchedule(param) }
        val today = testData.now.getDateWithoutTime()
        val todaySchedules =
            testData.weekSchedule.filter { it.datetime.getDateWithoutTime() == today }

        assertThat(shownSchedules).containsExactlyElementsOf(todaySchedules)

        verifyGetCurrentWeekSchedule(testData)
    }

    @Test
    fun `onScheduleClicked WILL navigate to reserve destination`() {
        val testData = TestData()
        setupGetCurrentWeekSchedule(testData)
        weekSchedulePresenter.bindView(view)

        weekSchedulePresenter.start()
            .andTriggerActions()

        val shownSchedules = view.capture { param: List<Schedule> -> showSchedule(param) }
        verifyGetCurrentWeekSchedule(testData)

        verifyNoMoreInteractions(view)
        reset(view)

        val schedule = shownSchedules.random()

        weekSchedulePresenter.onScheduleClicked(schedule)

        verify(navigator).navigateTo(ReserveDestination(schedule))
    }

    private fun verifyGetCurrentWeekSchedule(testData: TestData) {
        verify(getCurrentClubUseCase).invoke()
        verify(getCurrentWeekScheduleUseCase).invoke(testData.clubId)
        verify(dateProvider).getDate()
    }

    private fun setupGetCurrentWeekSchedule(testData: TestData) {
        given(getCurrentClubUseCase.invoke()).willReturn(Maybe.just(testData.clubId))
        given(dateProvider.getDate()).willReturn(testData.now)
        given(getCurrentWeekScheduleUseCase.invoke(testData.clubId)).willReturn(Single.just(testData.weekSchedule))
    }

    private data class TestData(
        val clubId: Int = getRandomInt(),
        val now: Date = Date()
    ) {
        val today = now.getDateWithoutTime()
        val firstDayOfWeek = firstDayOfWeek(today)
        val weekSchedule = (0..6).flatMap { dayOfWeek ->
            val scheduleDate = firstDayOfWeek.add(days = dayOfWeek)
            (1..23).map { hoursOfDay ->
                createSchedule().copy(datetime = scheduleDate.add(hours = hoursOfDay))
            }
        }
    }
}
