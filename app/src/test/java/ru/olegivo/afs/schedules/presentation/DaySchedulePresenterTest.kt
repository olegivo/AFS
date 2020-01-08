package ru.olegivo.afs.schedules.presentation

import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.reset
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.willReturn
import io.reactivex.Maybe
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import ru.olegivo.afs.BaseTestOf
import ru.olegivo.afs.common.add
import ru.olegivo.afs.common.domain.ErrorReporter
import ru.olegivo.afs.common.firstDayOfWeek
import ru.olegivo.afs.common.presentation.Navigator
import ru.olegivo.afs.helpers.capture
import ru.olegivo.afs.helpers.getRandomInt
import ru.olegivo.afs.helpers.getRandomString
import ru.olegivo.afs.schedule.presentation.models.ReserveDestination
import ru.olegivo.afs.schedules.domain.ActualizeScheduleUseCase
import ru.olegivo.afs.schedules.domain.GetDaySportsActivitiesUseCase
import ru.olegivo.afs.schedules.domain.models.SportsActivity
import ru.olegivo.afs.schedules.domain.models.createSportsActivity
import java.util.*
import kotlin.random.Random


class DaySchedulePresenterTest : BaseTestOf<DayScheduleContract.Presenter>() {

    override fun createInstance(): DayScheduleContract.Presenter = DaySchedulePresenter(
        getDaySportsActivitiesUseCase,
        actualizeScheduleUseCase,
        navigator,
        testScheduler,
        errorReporter
    )

    //<editor-fold desc="mocks">
    private val getDaySportsActivitiesUseCase: GetDaySportsActivitiesUseCase = mock()
    private val actualizeScheduleUseCase: ActualizeScheduleUseCase = mock()
    private val view: DayScheduleContract.View = mock()
    private val navigator: Navigator = mock()
    private val errorReporter: ErrorReporter = mock()

    override fun getAllMocks() = arrayOf(
        getDaySportsActivitiesUseCase,
        actualizeScheduleUseCase,
        view,
        navigator,
        errorReporter
    )
    //</editor-fold>

    @Test
    fun `start shows current day schedule with preEntry = true WHEN no errors, has current club, has schedule`() {
        val testData = TestData()
        setupGetCurrentWeekSchedule(testData)
        instance.bindView(view)
            .andTriggerActions()

        val shownSchedules = view.capture { param: List<SportsActivity> -> showSchedule(param) }
        val todaySchedules = testData.daySchedule//.filter { it.schedule.preEntry }

        assertThat(shownSchedules).containsExactlyElementsOf(todaySchedules)

        verifyGetDaySchedule(testData)
    }

    @Test
    fun `start shows error WHEN has error`() {
        val testData = TestData()
        val exception = RuntimeException()
        setupGetCurrentWeekSchedule(
            testData,
            dayScheduleProvider = { Maybe.error(exception) }
        )
        instance.bindView(view)
            .andTriggerActions()

        verifyGetDaySchedule(testData, expectedGetCurrentWeekSchedule = true)
        verify(view).showErrorMessage("Ошибка при получении расписания занятий на день")
        verify(errorReporter).reportError(exception, "Ошибка при получении расписания занятий на день")
    }

    @Test
    fun `start do nothing WHEN has no schedule`() {
        val testData = TestData()
        setupGetCurrentWeekSchedule(
            testData,
            dayScheduleProvider = { Maybe.empty() })
        instance.bindView(view)
            .andTriggerActions()

        verifyGetDaySchedule(testData)
    }

    @Test
    fun `onScheduleClicked WILL navigate to reserve destination`() {
        val testData = TestData()
        setupGetCurrentWeekSchedule(testData)
        instance.bindView(view)
            .andTriggerActions()

        val shownSportActivities =
            view.capture { param: List<SportsActivity> -> showSchedule(param) }
        verifyGetDaySchedule(testData)

        verifyNoMoreInteractions(view)
        reset(view)

        val sportsActivity = shownSportActivities.random()

        instance.onSportsActivityClicked(sportsActivity)

        verify(navigator).navigateTo(
            ReserveDestination(
                sportsActivity.schedule.id,
                sportsActivity.schedule.clubId
            )
        )
    }

    private fun verifyGetDaySchedule(
        testData: TestData,
        expectedGetCurrentWeekSchedule: Boolean = true
    ) {
        if (expectedGetCurrentWeekSchedule) {
            verify(getDaySportsActivitiesUseCase).invoke(testData.clubId, testData.day)
        }
        verify(view).showProgress()
        verify(view).hideProgress()
        verify(view).clubId
        verify(view).day
    }

    private fun setupGetCurrentWeekSchedule(
        testData: TestData,
        dayScheduleProvider: () -> Maybe<List<SportsActivity>> = {
            Maybe.just(testData.daySchedule)
        }
    ) {
        given { view.clubId }.willReturn { testData.clubId }
        given { view.day }.willReturn { testData.day }
        given(getDaySportsActivitiesUseCase.invoke(testData.clubId, testData.day))
            .willReturn { dayScheduleProvider() }
    }

    private data class TestData(
        val clubId: Int = getRandomInt(),
        val now: Date = Date()
    ) {
        val firstDayOfWeek = firstDayOfWeek()
        val dayOfWeek: Int = Random.nextInt(0, 7)
        val day = firstDayOfWeek.add(days = dayOfWeek)
        val daySchedule = (1..23).map { hoursOfDay ->
            createSportsActivity(datetime = day.add(hours = hoursOfDay))
        }
    }
}
