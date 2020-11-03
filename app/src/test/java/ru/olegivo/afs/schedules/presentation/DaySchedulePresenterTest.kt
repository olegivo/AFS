/*
 * Copyright (C) 2020 Oleg Ivashchenko <olegivo@gmail.com>
 *  
 * This file is part of AFS.
 *
 * AFS is free software: you can redistribute it and/or modify
 * it under the terms of the MIT License.
 *
 * AFS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * AFS.
 */

package ru.olegivo.afs.schedules.presentation

import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.reset
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.willReturn
import io.reactivex.Maybe
import io.reactivex.Scheduler
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import ru.olegivo.afs.analytics.domain.AnalyticsProvider
import ru.olegivo.afs.common.add
import ru.olegivo.afs.common.domain.ErrorReporter
import ru.olegivo.afs.common.firstDayOfWeek
import ru.olegivo.afs.common.presentation.BasePresenterTest
import ru.olegivo.afs.common.presentation.Navigator
import ru.olegivo.afs.extensions.toMaybe
import ru.olegivo.afs.helpers.capture
import ru.olegivo.afs.helpers.getRandomInt
import ru.olegivo.afs.helpers.willComplete
import ru.olegivo.afs.schedule.presentation.models.ReserveDestination
import ru.olegivo.afs.schedules.analytics.SchedulesAnalytic
import ru.olegivo.afs.schedules.domain.ActualizeScheduleUseCase
import ru.olegivo.afs.schedules.domain.GetDaySportsActivitiesUseCase
import ru.olegivo.afs.schedules.domain.models.SportsActivity
import ru.olegivo.afs.schedules.domain.models.createSportsActivity
import java.util.Date
import kotlin.random.Random

class DaySchedulePresenterTest :
    BasePresenterTest<DayScheduleContract.Presenter, DayScheduleContract.View>(DayScheduleContract.View::class) {

    override fun createPresenter(
        mainScheduler: Scheduler,
        errorReporter: ErrorReporter,
        analyticsProvider: AnalyticsProvider
    ) = DaySchedulePresenter(
        getDaySportsActivities = getDaySportsActivitiesUseCase,
        actualizeSchedule = actualizeScheduleUseCase,
        navigator = navigator,
        mainScheduler = testScheduler,
        errorReporter = errorReporter,
        analyticsProvider = analyticsProvider
    )

    //<editor-fold desc="mocks">
    private val getDaySportsActivitiesUseCase: GetDaySportsActivitiesUseCase = mock()
    private val actualizeScheduleUseCase: ActualizeScheduleUseCase = mock()
    private val navigator: Navigator = mock()

    override fun getPresenterMocks() = arrayOf(
        getDaySportsActivitiesUseCase,
        actualizeScheduleUseCase,
        navigator
    )
    //</editor-fold>

    @Test
    fun `start shows current day schedule with preEntry = true WHEN no errors, has current club, has schedule`() =
        bind(TestData()) {
            prepare {
                setupGetCurrentWeekSchedule()
            }

            verify {
                val shownSchedules =
                    view.capture { param: List<SportsActivity> -> showSchedule(param) }
                val todaySchedules = daySchedule//.filter { it.schedule.preEntry }

                assertThat(shownSchedules).containsExactlyElementsOf(todaySchedules)

                this.verifyGetDaySchedule()
            }
        }

    @Test
    fun `start shows error WHEN has error`() =
        bind(TestData(getCurrentWeekScheduleException = RuntimeException())) {
            prepare {
                setupGetCurrentWeekSchedule()
            }

            verify {
                verifyGetDaySchedule(expectedGetCurrentWeekSchedule = true)
                verify(view).showErrorMessage("Ошибка при получении расписания занятий на день")
                verify(errorReporter)
                    .reportError(
                        getCurrentWeekScheduleException!!,
                        "Ошибка при получении расписания занятий на день"
                    )
            }
        }

    @Test
    fun `start do nothing WHEN has no schedule`() {
        val testData = TestData(dayScheduleProvider = { Maybe.empty() })
        bind(testData) {
            prepare {
                setupGetCurrentWeekSchedule()
            }

            verify {
                verifyGetDaySchedule()
            }
        }
    }

    @Test
    fun `onScheduleClicked WILL navigate to reserve destination`() {
        bind(TestData()) {
            prepare {
                setupGetCurrentWeekSchedule()
            }
            verify {
                verifyGetDaySchedule()
            }
        }

        val shownSportActivities =
            view.capture { param: List<SportsActivity> -> showSchedule(param) }

        verifyNoMoreInteractions(view)
        reset(view)

        val sportsActivity = shownSportActivities.random()
        given { analyticsProvider.logEvent(SchedulesAnalytic.Screens.DaySchedule.OnSportsActivityClicked) }
            .willComplete()

        instance.onSportsActivityClicked(sportsActivity)

        verify(navigator).navigateTo(
            ReserveDestination(
                sportsActivity.schedule.id,
                sportsActivity.schedule.clubId
            )
        )
        verify(analyticsProvider).logEvent(SchedulesAnalytic.Screens.DaySchedule.OnSportsActivityClicked)
    }

    private fun TestData.setupGetCurrentWeekSchedule() {
        given { view.clubId }.willReturn { clubId }
        given { view.day }.willReturn { day }
        given(getDaySportsActivitiesUseCase.invoke(clubId, day))
            .willReturn {
                getCurrentWeekScheduleException?.let { Maybe.error(it) }
                    ?: this.dayScheduleProvider?.invoke()
                    ?: run { daySchedule.toMaybe() }
            }
    }

    private fun TestData.verifyGetDaySchedule(expectedGetCurrentWeekSchedule: Boolean = true) {
        if (expectedGetCurrentWeekSchedule) {
            verify(getDaySportsActivitiesUseCase).invoke(clubId, day)
        }
        verify(view).showProgress()
        verify(view).hideProgress()
        verify(view).clubId
        verify(view).day
    }

    private data class TestData(
        val clubId: Int = getRandomInt(),
        val now: Date = Date(),
        val dayScheduleProvider: (() -> Maybe<List<SportsActivity>>)? = null,
        val getCurrentWeekScheduleException: Throwable? = null
    ) {
        val firstDayOfWeek = firstDayOfWeek()
        val dayOfWeek: Int = Random.nextInt(0, 7)
        val day = firstDayOfWeek.add(days = dayOfWeek)
        val daySchedule = (1..23).map { hoursOfDay ->
            createSportsActivity(datetime = day.add(hours = hoursOfDay))
        }
    }
}
