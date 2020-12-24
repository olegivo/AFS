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
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.willReturn
import io.reactivex.Maybe
import io.reactivex.Scheduler
import org.junit.Test
import ru.olegivo.afs.analytics.domain.AnalyticsProvider
import ru.olegivo.afs.clubs.domain.GetCurrentClubUseCase
import ru.olegivo.afs.common.add
import ru.olegivo.afs.common.domain.DateProvider
import ru.olegivo.afs.common.domain.ErrorReporter
import ru.olegivo.afs.common.firstDayOfWeek
import ru.olegivo.afs.common.presentation.BasePresenterTest
import ru.olegivo.afs.common.presentation.Navigator
import ru.olegivo.afs.extensions.toMaybe
import ru.olegivo.afs.helpers.getRandomInt
import kotlin.random.Random

class WeekSchedulePresenterTest :
    BasePresenterTest<WeekScheduleContract.Presenter, WeekScheduleContract.View>(
        WeekScheduleContract.View::class
    ) {

    override fun createPresenter(
        mainScheduler: Scheduler,
        errorReporter: ErrorReporter,
        analyticsProvider: AnalyticsProvider
    ) = WeekSchedulePresenter(
        getCurrentClub = getCurrentClubUseCase,
        dateProvider = dateProvider,
        navigator = navigator,
        mainScheduler = testScheduler,
        errorReporter = errorReporter,
        analyticsProvider = analyticsProvider
    )

    //<editor-fold desc="mocks">
    private val getCurrentClubUseCase: GetCurrentClubUseCase = mock()
    private val dateProvider: DateProvider = mock()
    private val navigator: Navigator = mock()

    override fun getPresenterMocks() = arrayOf(
        getCurrentClubUseCase,
        dateProvider,
        navigator
    )
    //</editor-fold>

    private val weekWeekSchedulePresenter get() = instance as WeekSchedulePresenter

    override fun setUp() {
        super.setUp()
        weekWeekSchedulePresenter.clear()
    }

    @Test
    fun `start SHOWS current day WHEN no errors, has current club`() =
        bind(TestData()) {
            prepare {
                setupGetCurrentClub()
            }

            verify {
                verifyGetCurrentClub()
            }
        }

    @Test
    fun `start SHOWS error WHEN has error`() {
        val message = "Ошибка при получении текущего клуба"
        val exception = RuntimeException()
        val testData = TestData(currentClubIdMaybeProvider = { Maybe.error(exception) })

        bind(testData) {
            prepare {
                setupGetCurrentClub()
            }

            verify {
                verifyGetCurrentClub(expectedWeekDaysReady = false)
                verify(view).showErrorMessage(message)
                verify(errorReporter).reportError(exception, message)
            }
        }
    }

    @Test
    fun `start do nothing WHEN has no current clubId`() =
        bind(TestData(currentClubIdMaybeProvider = { Maybe.empty() })) {
            prepare {
                setupGetCurrentClub()
            }

            verify {
                verifyGetCurrentClub(expectedWeekDaysReady = false)
            }
        }

    private fun TestData.setupGetCurrentClub() {
        given(getCurrentClubUseCase.invoke()).willReturn { this.currentClubIdMaybeProvider() }
        given(dateProvider.getDate()).willReturn { now }
        given(dateProvider.getCurrentWeekDayNumber()).willReturn(initialPosition + 1)
    }

    private fun TestData.verifyGetCurrentClub(expectedWeekDaysReady: Boolean = true) {
        verify(dateProvider).getDate()
        verify(dateProvider).getCurrentWeekDayNumber()
        verify(getCurrentClubUseCase).invoke()
        if (expectedWeekDaysReady) {
            verify(view).onReady(initialPosition)
        }
        verify(view).showProgress()
        verify(view).hideProgress()
    }

    private class TestData(
        val clubId: Int = getRandomInt(),
        val currentClubIdMaybeProvider: () -> Maybe<Int> = { clubId.toMaybe() }
    ) {
        val firstDayOfWeek = firstDayOfWeek()
        val initialPosition: Int = Random.nextInt(0, 6)
        val now =
            firstDayOfWeek.add(days = initialPosition, hours = Random.nextInt(0, 23))
    }
}
