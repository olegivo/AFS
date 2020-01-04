package ru.olegivo.afs.schedules.presentation

import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.willReturn
import io.reactivex.Maybe
import org.junit.Test
import ru.olegivo.afs.BaseTestOf
import ru.olegivo.afs.clubs.domain.GetCurrentClubUseCase
import ru.olegivo.afs.common.add
import ru.olegivo.afs.common.domain.DateProvider
import ru.olegivo.afs.common.firstDayOfWeek
import ru.olegivo.afs.common.presentation.Navigator
import ru.olegivo.afs.helpers.getRandomInt
import ru.olegivo.afs.helpers.getRandomString
import ru.olegivo.afs.schedules.domain.ActualizeScheduleUseCase
import kotlin.random.Random


class WeekSchedulePresenterTest : BaseTestOf<WeekScheduleContract.Presenter>() {

    override fun createInstance(): WeekScheduleContract.Presenter = weekWeekSchedulePresenter

    //<editor-fold desc="mocks">
    private val getCurrentClubUseCase: GetCurrentClubUseCase = mock()
    private val actualizeScheduleUseCase: ActualizeScheduleUseCase = mock()
    private val view: WeekScheduleContract.View = mock()
    private val dateProvider: DateProvider = mock()
    private val navigator: Navigator = mock()

    override fun getAllMocks() = arrayOf(
        getCurrentClubUseCase,
        actualizeScheduleUseCase,
        view,
        dateProvider,
        navigator
    )
    //</editor-fold>

    private val weekWeekSchedulePresenter = WeekSchedulePresenter(
        getCurrentClubUseCase,
        actualizeScheduleUseCase,
        dateProvider,
        navigator,
        schedulerRule.testScheduler
    )

    override fun setUp() {
        super.setUp()
        weekWeekSchedulePresenter.clear()
    }

    @Test
    fun `start SHOWS current day WHEN no errors, has current club`() {
        val testData = TestData()
        setupGetCurrentWeekSchedule(testData)
        weekWeekSchedulePresenter.bindView(view)
            .andTriggerActions()

        verifyGetCurrentWeekSchedule(testData)
    }

    @Test
    fun `start SHOWS error WHEN has error`() {
        val testData = TestData()
        val message = getRandomString()
        val exception = RuntimeException(message)
        setupGetCurrentWeekSchedule(
            testData,
            currentClubIdMaybeProvider = { Maybe.error(exception) })
        weekWeekSchedulePresenter.bindView(view)
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
        weekWeekSchedulePresenter.bindView(view)
            .andTriggerActions()

        verifyGetCurrentWeekSchedule(testData, expectedGetCurrentWeekSchedule = false)
    }

    private fun verifyGetCurrentWeekSchedule(
        testData: TestData,
        expectedGetCurrentWeekSchedule: Boolean = true
    ) {
        verify(dateProvider).getDate()
        verify(getCurrentClubUseCase).invoke()
        if (expectedGetCurrentWeekSchedule) {
            verify(view).onReady(testData.initialPosition)
        }
        verify(view).showProgress()
        verify(view).hideProgress()
    }

    private fun setupGetCurrentWeekSchedule(
        testData: TestData,
        currentClubIdMaybeProvider: () -> Maybe<Int> = {
            Maybe.just(testData.clubId)
        }
    ) {
        given(getCurrentClubUseCase.invoke()).willReturn { currentClubIdMaybeProvider() }
        given(dateProvider.getDate()).willReturn { testData.now }
    }

    private class TestData(val clubId: Int = getRandomInt()) {
        val firstDayOfWeek = firstDayOfWeek()
        val initialPosition: Int = Random.nextInt(0, 7)
        val now = firstDayOfWeek.add(days = initialPosition, hours = Random.nextInt(0, 23))
    }
}
