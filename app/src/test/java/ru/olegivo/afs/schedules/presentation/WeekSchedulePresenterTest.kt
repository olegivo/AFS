package ru.olegivo.afs.schedules.presentation

import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.reset
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import io.reactivex.Maybe
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
import ru.olegivo.afs.helpers.getRandomString
import ru.olegivo.afs.schedule.presentation.models.ReserveDestination
import ru.olegivo.afs.schedules.domain.GetCurrentWeekScheduleUseCase
import ru.olegivo.afs.schedules.domain.models.SportsActivity
import ru.olegivo.afs.schedules.domain.models.createSportsActivity
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
    fun `start shows current day schedule with preEntry = true WHEN no errors, has current club, has schedule`() {
        val testData = TestData()
        setupGetCurrentWeekSchedule(testData)
        weekSchedulePresenter.bindView(view)

        weekSchedulePresenter.start()
            .andTriggerActions()

        val shownSchedules = view.capture { param: List<SportsActivity> -> showSchedule(param) }
        val today = testData.now.getDateWithoutTime()
        val todaySchedules =
            testData.weekSchedule.filter { it.schedule.preEntry && it.schedule.datetime.getDateWithoutTime() == today }

        assertThat(shownSchedules).containsExactlyElementsOf(todaySchedules)

        verifyGetCurrentWeekSchedule(testData)
    }

    @Test
    fun `start shows error WHEN has error`() {
        val testData = TestData()
        val message = getRandomString()
        val exception = RuntimeException(message)
        setupGetCurrentWeekSchedule(
            testData,
            currentClubIdMaybeProvider = { Maybe.error(exception) })
        weekSchedulePresenter.bindView(view)

        weekSchedulePresenter.start()
            .andTriggerActions()

        verifyGetCurrentWeekSchedule(testData, expectedGetCurrentWeekSchedule = false)
        verify(view).showErrorMessage(message)
    }

    @Test
    fun `start do nothing WHEN has no current clubId`() {
        val testData = TestData()
        setupGetCurrentWeekSchedule(
            testData,
            currentClubIdMaybeProvider = { Maybe.empty() })
        weekSchedulePresenter.bindView(view)

        weekSchedulePresenter.start()
            .andTriggerActions()

        verifyGetCurrentWeekSchedule(testData, expectedGetCurrentWeekSchedule = false)
    }

    @Test
    fun `start do nothing WHEN has no schedule`() {
        val testData = TestData()
        setupGetCurrentWeekSchedule(
            testData,
            currentWeekScheduleProvider = { Maybe.empty() })
        weekSchedulePresenter.bindView(view)

        weekSchedulePresenter.start()
            .andTriggerActions()

        verifyGetCurrentWeekSchedule(testData, expectedProcessCurrentWeekSchedule = false)
    }

    @Test
    fun `onScheduleClicked WILL navigate to reserve destination`() {
        val testData = TestData()
        setupGetCurrentWeekSchedule(testData)
        weekSchedulePresenter.bindView(view)

        weekSchedulePresenter.start()
            .andTriggerActions()

        val shownSchedules = view.capture { param: List<SportsActivity> -> showSchedule(param) }
        verifyGetCurrentWeekSchedule(testData)

        verifyNoMoreInteractions(view)
        reset(view)

        val schedule = shownSchedules.random()

        weekSchedulePresenter.onSportsActivityClicked(schedule)

        verify(navigator).navigateTo(ReserveDestination(schedule))
    }

    private fun verifyGetCurrentWeekSchedule(
        testData: TestData,
        expectedGetCurrentWeekSchedule: Boolean = true,
        expectedProcessCurrentWeekSchedule: Boolean = true
    ) {
        verify(getCurrentClubUseCase).invoke()
        if (expectedGetCurrentWeekSchedule) {
            verify(getCurrentWeekScheduleUseCase).invoke(testData.clubId)
            if (expectedProcessCurrentWeekSchedule) verify(dateProvider).getDate()
        }
        verify(view).showProgress()
        verify(view).hideProgress()
    }

    private fun setupGetCurrentWeekSchedule(
        testData: TestData,
        currentClubIdMaybeProvider: () -> Maybe<Int> = {
            Maybe.just(testData.clubId)
        },
        currentWeekScheduleProvider: () -> Maybe<List<SportsActivity>> = {
            Maybe.just(testData.weekSchedule)
        }
    ) {
        given(getCurrentClubUseCase.invoke()).willReturn(currentClubIdMaybeProvider())
        given(dateProvider.getDate()).willReturn(testData.now)
        given(getCurrentWeekScheduleUseCase.invoke(testData.clubId)).willReturn(
            currentWeekScheduleProvider()
        )
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
                createSportsActivity(datetime = scheduleDate.add(hours = hoursOfDay))
            }
        }
    }
}
