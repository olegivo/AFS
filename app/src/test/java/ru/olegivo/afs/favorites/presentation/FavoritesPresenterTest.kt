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

package ru.olegivo.afs.favorites.presentation

import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.Maybe
import io.reactivex.Scheduler
import io.reactivex.Single
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import ru.olegivo.afs.analytics.domain.AnalyticsProvider
import ru.olegivo.afs.common.domain.ErrorReporter
import ru.olegivo.afs.common.presentation.BasePresenterTest
import ru.olegivo.afs.common.presentation.Navigator
import ru.olegivo.afs.extensions.toMaybe
import ru.olegivo.afs.extensions.toSingle
import ru.olegivo.afs.favorites.data.models.createFavoriteFilter
import ru.olegivo.afs.favorites.domain.GetClosestSportsActivityUseCase
import ru.olegivo.afs.favorites.domain.GetFavoritesUseCase
import ru.olegivo.afs.favorites.domain.models.toFavoriteFilter
import ru.olegivo.afs.favorites.presentation.models.FavoritesItem
import ru.olegivo.afs.favorites.presentation.models.createFavoriteFilterItem
import ru.olegivo.afs.helpers.capture
import ru.olegivo.afs.repeat
import ru.olegivo.afs.schedule.presentation.models.ReserveDestination
import ru.olegivo.afs.schedules.domain.models.createSportsActivity
import java.util.Calendar
import java.util.Locale

class FavoritesPresenterTest :
    BasePresenterTest<FavoritesContract.Presenter, FavoritesContract.View>(FavoritesContract.View::class) {

    override fun createPresenter(
        mainScheduler: Scheduler,
        errorReporter: ErrorReporter,
        analyticsProvider: AnalyticsProvider
    ): FavoritesContract.Presenter =
        FavoritesPresenter(
            getFavorites = getFavoritesUseCase,
            getClosestSportsActivity = getClosestSportsActivityUseCase,
            mainScheduler = mainScheduler,
            locale = Locale.getDefault(),
            navigator = navigator,
            errorReporter = errorReporter,
            analyticsProvider = analyticsProvider
        )

    //<editor-fold desc="Mocks">
    private val getFavoritesUseCase: GetFavoritesUseCase = mock()
    private val getClosestSportsActivityUseCase: GetClosestSportsActivityUseCase = mock()
    private val navigator: Navigator = mock()

    override fun getPresenterMocks() = arrayOf(
        getFavoritesUseCase,
        getClosestSportsActivityUseCase,
        navigator
    )
    //</editor-fold>

    override fun verifyBindInteractions() {
        super.verifyBindInteractions()
        verify(getFavoritesUseCase).invoke()
    }

    @Test
    fun `bindView SHOWS error WHEN fetch error happened`() {
        val exception = RuntimeException("test")
        given { getFavoritesUseCase.invoke() }.willReturn(Single.error(exception))

        bind()

        verifyError(exception, "Ошибка при получении списка избранных занятий")
    }

    @Test
    fun `bindView SHOWS favorite items WHEN fetch success`() {
        val favorites = { createFavoriteFilter() }.repeat(3)
        given { getFavoritesUseCase.invoke() }.willReturn(favorites.toSingle())

        bind()

        val list = view.capture { arg: List<FavoritesItem> -> showFavorites(arg) }
        assertThat(list.map { it.filter }).containsExactlyElementsOf(favorites)
    }

    @Test
    fun `bindView BINDS favorite duty correctly`() {
        val hours = 13
        val minutes = 13
        val minutesOfDay = hours * 60 + minutes
        val filter = createFavoriteFilter().copy(
            minutesOfDay = minutesOfDay,
            dayOfWeek = Calendar.FRIDAY
        )
        val favorites = listOf(filter)
        given { getFavoritesUseCase.invoke() }.willReturn(favorites.toSingle())

        bind()

        val list = view.capture { arg: List<FavoritesItem> -> showFavorites(arg) }
        assertThat(list.map { it.filter }).containsOnly(filter)

        val actual = list.single()
        val fridayName = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY)
        }.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT_FORMAT, Locale.getDefault())
        assertThat(actual.duty).isEqualTo("$fridayName, 13:13")
    }

    @Test
    fun `onItemClick NAVIGATES to closest sports activity WHEN has closest sports activity`() {
        val sportsActivity = createSportsActivity()
        val schedule = sportsActivity.schedule
        val favoriteFilter = schedule.toFavoriteFilter()
        val favoritesItem = createFavoriteFilterItem(filter = favoriteFilter)

        given { getClosestSportsActivityUseCase.invoke(favoriteFilter, schedule.clubId) }
            .willReturn(schedule.id.toMaybe())

        instance.onItemClick(favoritesItem)
            .andTriggerActions()

        val destination = ReserveDestination(
            id = schedule.id,
            clubId = schedule.clubId
        )

        verify(navigator).navigateTo(destination)
        verify(getClosestSportsActivityUseCase).invoke(favoriteFilter, schedule.clubId)
    }

    @Test
    fun `onItemClick DOES nothing WHEN has no closest sports activity`() {
        val sportsActivity = createSportsActivity()
        val schedule = sportsActivity.schedule
        val favoriteFilter = schedule.toFavoriteFilter()
        val favoritesItem = createFavoriteFilterItem(filter = favoriteFilter)

        given { getClosestSportsActivityUseCase.invoke(favoriteFilter, schedule.clubId) }
            .willReturn(Maybe.empty())

        instance.onItemClick(favoritesItem)
            .andTriggerActions()

        verify(getClosestSportsActivityUseCase).invoke(favoriteFilter, schedule.clubId)
    }
}
