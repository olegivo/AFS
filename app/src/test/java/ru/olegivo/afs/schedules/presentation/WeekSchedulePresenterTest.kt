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
import org.junit.Test
import ru.olegivo.afs.BaseTestOf
import ru.olegivo.afs.clubs.domain.GetCurrentClubUseCase
import ru.olegivo.afs.common.add
import ru.olegivo.afs.common.domain.DateProvider
import ru.olegivo.afs.common.domain.ErrorReporter
import ru.olegivo.afs.common.firstDayOfWeek
import ru.olegivo.afs.common.presentation.Navigator
import ru.olegivo.afs.helpers.getRandomInt
import kotlin.random.Random

class WeekSchedulePresenterTest : BaseTestOf<WeekScheduleContract.Presenter>() {

    override fun createInstance(): WeekScheduleContract.Presenter = weekWeekSchedulePresenter

    //<editor-fold desc="mocks">
    private val getCurrentClubUseCase: GetCurrentClubUseCase = mock()
    private val view: WeekScheduleContract.View = mock()
    private val dateProvider: DateProvider = mock()
    private val navigator: Navigator = mock()
    private val errorReporter: ErrorReporter = mock()

    override fun getAllMocks() = arrayOf(
        getCurrentClubUseCase,
        view,
        dateProvider,
        navigator,
        errorReporter
    )
    //</editor-fold>

    private val weekWeekSchedulePresenter = WeekSchedulePresenter(
        getCurrentClubUseCase,
        dateProvider,
        navigator,
        schedulerRule.testScheduler,
        errorReporter
    )

    override fun setUp() {
        super.setUp()
        weekWeekSchedulePresenter.clear()
    }

    @Test
    fun `start SHOWS current day WHEN no errors, has current club`() {
        val testData = TestData()
        setupGetCurrentClub(testData)
        instance.bindView(view)
            .andTriggerActions()

        verifyGetCurrentClub(testData)
    }

    @Test
    fun `start SHOWS error WHEN has error`() {
        val testData = TestData()
        val message = "Ошибка при получении текущего клуба"
        val exception = RuntimeException()
        setupGetCurrentClub(
            testData,
            currentClubIdMaybeProvider = { Maybe.error(exception) }
        )
        instance.bindView(view)
            .andTriggerActions()

        verifyGetCurrentClub(testData, expectedWeekDaysReady = false)
        verify(view).showErrorMessage(message)
        verify(errorReporter).reportError(exception, message)
    }

    @Test
    fun `start do nothing WHEN has no current clubId`() {
        val testData = TestData()
        setupGetCurrentClub(
            testData,
            currentClubIdMaybeProvider = { Maybe.empty() }
        )
        instance.bindView(view)
            .andTriggerActions()

        verifyGetCurrentClub(testData, expectedWeekDaysReady = false)
    }

    private fun verifyGetCurrentClub(
        testData: TestData,
        expectedWeekDaysReady: Boolean = true
    ) {
        verify(dateProvider).getDate()
        verify(getCurrentClubUseCase).invoke()
        if (expectedWeekDaysReady) {
            verify(view).onReady(testData.initialPosition)
        }
        verify(view).showProgress()
        verify(view).hideProgress()
    }

    private fun setupGetCurrentClub(
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
