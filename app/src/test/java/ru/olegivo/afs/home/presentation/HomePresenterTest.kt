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

package ru.olegivo.afs.home.presentation

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.Scheduler
import org.junit.Test
import ru.olegivo.afs.analytics.domain.AnalyticsProvider
import ru.olegivo.afs.common.domain.ErrorReporter
import ru.olegivo.afs.common.presentation.BasePresenterTest
import ru.olegivo.afs.common.presentation.Navigator
import ru.olegivo.afs.favorites.presentation.models.FavoritesDestination
import ru.olegivo.afs.schedules.presentation.models.ScheduleDestination
import ru.olegivo.afs.settings.navigation.SettingsDestination

class HomePresenterTest :
    BasePresenterTest<HomeContract.Presenter, HomeContract.View>(HomeContract.View::class) {

    override fun createPresenter(
        mainScheduler: Scheduler,
        errorReporter: ErrorReporter,
        analyticsProvider: AnalyticsProvider
    ) = HomePresenter(navigator, errorReporter, analyticsProvider)

    //<editor-fold desc="Mocks">
    private val navigator: Navigator = mock()

    override fun getPresenterMocks(): Array<Any> = arrayOf(navigator)
    //</editor-fold>

    @Test
    fun `onFavoritesClicked NAVIGATES to favorites`() {
        instance.onFavoritesClicked()
        verify(navigator).navigateTo(FavoritesDestination)
    }

    @Test
    fun `onSettingsClicked NAVIGATES to setings`() {
        instance.onSettingsClicked()
        verify(navigator).navigateTo(SettingsDestination)
    }

    @Test
    fun `onSchedulesClicked NAVIGATES to schedules`() {
        instance.onSchedulesClicked()
        verify(navigator).navigateTo(ScheduleDestination)
    }
}
