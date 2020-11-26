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

import ru.olegivo.afs.analytics.domain.AnalyticsProvider
import ru.olegivo.afs.common.domain.ErrorReporter
import ru.olegivo.afs.common.presentation.BasePresenter
import ru.olegivo.afs.common.presentation.Navigator
import ru.olegivo.afs.favorites.presentation.models.FavoritesDestination
import ru.olegivo.afs.schedules.presentation.models.ScheduleDestination
import ru.olegivo.afs.settings.navigation.SettingsDestination
import javax.inject.Inject

class HomePresenter @Inject constructor(
    private val navigator: Navigator,
    errorReporter: ErrorReporter,
    analyticsProvider: AnalyticsProvider
) :
    BasePresenter<HomeContract.View>(errorReporter, analyticsProvider),
    HomeContract.Presenter {

    override fun onSettingsClicked() {
        navigator.navigateTo(SettingsDestination)
    }

    override fun onFavoritesClicked() {
        navigator.navigateTo(FavoritesDestination)
    }

    override fun onSchedulesClicked() {
        navigator.navigateTo(ScheduleDestination)
    }
}
