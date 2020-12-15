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

package ru.olegivo.afs.settings.presentation

import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.reset
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import io.reactivex.Maybe
import io.reactivex.Scheduler
import org.junit.Test
import ru.olegivo.afs.analytics.domain.AnalyticsProvider
import ru.olegivo.afs.clubs.domain.GetClubsUseCase
import ru.olegivo.afs.clubs.domain.GetCurrentClubUseCase
import ru.olegivo.afs.clubs.domain.SetCurrentClubUseCase
import ru.olegivo.afs.clubs.domain.models.createClub
import ru.olegivo.afs.common.domain.ErrorReporter
import ru.olegivo.afs.common.presentation.BasePresenterTest
import ru.olegivo.afs.extensions.toMaybe
import ru.olegivo.afs.extensions.toSingle
import ru.olegivo.afs.helpers.getRandomBoolean
import ru.olegivo.afs.helpers.willComplete
import ru.olegivo.afs.repeat
import ru.olegivo.afs.schedule.domain.ReserveRepository
import ru.olegivo.afs.settings.domain.DeleteDatabaseUseCase

class SettingsPresenterTest :
    BasePresenterTest<SettingsContract.Presenter, SettingsContract.View>(SettingsContract.View::class) {

    override fun createPresenter(
        mainScheduler: Scheduler,
        errorReporter: ErrorReporter,
        analyticsProvider: AnalyticsProvider
    ): SettingsContract.Presenter = SettingsPresenter(
        reserveRepository = reserveRepository,
        getClubs = getClubsUseCase,
        getCurrentClub = getCurrentClubUseCase,
        setCurrentClub = setCurrentClubUseCase,
        deleteDatabase = deleteDatabaseUseCase,
        mainScheduler = mainScheduler,
        errorReporter = errorReporter,
        analyticsProvider = analyticsProvider
    )

    private val reserveRepository: ReserveRepository = mock()
    private val getClubsUseCase: GetClubsUseCase = mock()
    private val getCurrentClubUseCase: GetCurrentClubUseCase = mock()
    private val setCurrentClubUseCase: SetCurrentClubUseCase = mock()
    private val deleteDatabaseUseCase: DeleteDatabaseUseCase = mock()

    override fun getPresenterMocks(): Array<Any> = arrayOf(
        reserveRepository,
        getClubsUseCase,
        getCurrentClubUseCase,
        setCurrentClubUseCase,
        deleteDatabaseUseCase
    )

    override fun setUp() {
        super.setUp()
        (instance as SettingsPresenter).reset() // TODO: extract interface (can reset state for test purposes)
    }

    @Test
    fun `bindView SETS stub reserve`() {
        bind(getRandomBoolean()) {
            prepare {
                given { reserveRepository.isStubReserve() }.willReturn(toSingle())
            }
            verify {
                verify(reserveRepository).isStubReserve()
                verify(view).enableIsStubReserveCheckBox(this)
            }
        }
    }

    @Test
    fun `onChooseClubClicked SHOWS choose club dialog with selected club WHEN has previously selected club, view bound`() {
        bindView()

        val clubs = { createClub() }.repeat(2)
        given { getClubsUseCase.invoke() }.willReturn(clubs.toSingle())
        val currentClub = clubs.random()
        given { getCurrentClubUseCase.invoke() }.willReturn(currentClub.id.toMaybe())

        instance.onChooseClubClicked()
            .andTriggerActions()

        verify(getClubsUseCase).invoke()
        verify(getCurrentClubUseCase).invoke()
        verify(view).showChooseClubDialog(clubs, currentClub)
    }

    @Test
    fun `onChooseClubClicked SHOWS choose club dialogwithout selected club WHEN has not previously selected club, view bound`() {
        bindView()

        val clubs = { createClub() }.repeat(2)
        given { getClubsUseCase.invoke() }.willReturn(clubs.toSingle())
        given { getCurrentClubUseCase.invoke() }.willReturn(Maybe.empty())

        instance.onChooseClubClicked()
            .andTriggerActions()

        verify(getClubsUseCase).invoke()
        verify(getCurrentClubUseCase).invoke()
        verify(view).showChooseClubDialog(clubs, null)
    }

    @Test
    fun `onCurrentClubSelected SETS the club as current, view bound`() {
        bindView()

        val club = createClub()
        given { setCurrentClubUseCase.invoke(club.id) }.willComplete()

        instance.onCurrentClubSelected(club)
            .andTriggerActions()

        verify(setCurrentClubUseCase).invoke(club.id)
        verify(view).showMessage("Current club is ${club.title}")
    }

    @Test
    fun `onSetDefaultClubClicked SETS default club as current, view bound`() {
        bindView()

        given { setCurrentClubUseCase.invoke(SettingsPresenter.DefaultClubId) }.willComplete()

        instance.onSetDefaultClubClicked()
            .andTriggerActions()

        verify(setCurrentClubUseCase).invoke(SettingsPresenter.DefaultClubId)
        verify(view).showMessage("Выбран клуб по умолчанию")
    }

    @Test
    fun `onDropDbBClicked DROPS database WHEN view bound`() {
        bindView()

        given { deleteDatabaseUseCase.invoke() }.willComplete()

        instance.onDropDbBClicked()
            .andTriggerActions()

        verify(deleteDatabaseUseCase).invoke()
        verify(view).showMessage("БД удалена")
    }

    @Test
    fun `onStubReserveChecked  WHEN view bound`() {
        val isStubReserve = getRandomBoolean()
        bindView(isStubReserve = isStubReserve)
        verifyNoMoreInteractions(view)
        reset(view)

        given { reserveRepository.setStubReserve(isStubReserve) }.willComplete()

        instance.onStubReserveChecked(isStubReserve)
            .andTriggerActions()

        verify(reserveRepository).setStubReserve(isStubReserve)
        verify(view).disableIsStubReserveCheckBox()
        verify(view).enableIsStubReserveCheckBox(isStubReserve)
    }

    private fun bindView(isStubReserve: Boolean = getRandomBoolean()) {
        bind(Unit) {
            prepare {
                given { reserveRepository.isStubReserve() }.willReturn(isStubReserve.toSingle())
            }
            verify {
                verify(reserveRepository).isStubReserve()
                verify(view).enableIsStubReserveCheckBox(isStubReserve)
            }
        }
    }
}
