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

package ru.olegivo.afs.schedule.presentation

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.reset
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.willReturn
import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.Single
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import ru.olegivo.afs.common.add
import ru.olegivo.afs.common.domain.DateProvider
import ru.olegivo.afs.common.domain.ErrorReporter
import ru.olegivo.afs.common.presentation.BasePresenterTest
import ru.olegivo.afs.common.presentation.Navigator
import ru.olegivo.afs.extensions.toMaybe
import ru.olegivo.afs.extensions.toSingle
import ru.olegivo.afs.favorites.domain.AddToFavoritesUseCase
import ru.olegivo.afs.favorites.domain.PlanFavoriteRecordReminderUseCase
import ru.olegivo.afs.helpers.capture
import ru.olegivo.afs.helpers.getRandomString
import ru.olegivo.afs.schedule.domain.GetSportsActivityUseCase
import ru.olegivo.afs.schedule.domain.RemoveFromFavoritesUseCase
import ru.olegivo.afs.schedule.domain.ReserveUseCase
import ru.olegivo.afs.schedule.domain.SavedAgreementUseCase
import ru.olegivo.afs.schedule.domain.SavedReserveContactsUseCase
import ru.olegivo.afs.schedule.domain.models.ReserveContacts
import ru.olegivo.afs.schedule.domain.models.ReserveResult
import ru.olegivo.afs.schedules.domain.models.SportsActivity
import ru.olegivo.afs.schedules.domain.models.createReserveContacts
import ru.olegivo.afs.schedules.domain.models.createSportsActivity
import ru.olegivo.afs.schedules.presentation.models.SportsActivityDisplay
import ru.olegivo.afs.schedules.presentation.models.toDisplay
import java.util.Date
import java.util.Locale

class ScheduleDetailsPresenterTest :
    BasePresenterTest<ScheduleDetailsContract.Presenter, ScheduleDetailsContract.View>(
        ScheduleDetailsContract.View::class
    ) {

    override fun createPresenter(mainScheduler: Scheduler, errorReporter: ErrorReporter) =
        ScheduleDetailsPresenter(
            reserveUseCase = reserveUseCase,
            getSportsActivity = getSportsActivityUseCase,
            savedReserveContactsUseCase = savedReserveContactsUseCase,
            savedAgreementUseCase = savedAgreementUseCase,
            addToFavorites = addToFavoritesUseCase,
            removeFromFavorites = removeFromFavoritesUseCase,
            mainScheduler = schedulerRule.testScheduler,
            navigator = navigator,
            planFavoriteRecordReminderUseCase = planFavoriteRecordReminderUseCase,
            locale = Locale("ru"),
            dateProvider = dateProvider,
            errorReporter = errorReporter
        )

    //<editor-fold desc="mocks">
    private val reserveUseCase: ReserveUseCase = mock()
    private val getSportsActivityUseCase: GetSportsActivityUseCase = mock()
    private val savedReserveContactsUseCase: SavedReserveContactsUseCase = mock()
    private val savedAgreementUseCase: SavedAgreementUseCase = mock()
    private val addToFavoritesUseCase: AddToFavoritesUseCase = mock()
    private val removeFromFavoritesUseCase: RemoveFromFavoritesUseCase = mock()
    private val navigator: Navigator = mock()
    private val planFavoriteRecordReminderUseCase: PlanFavoriteRecordReminderUseCase = mock()
    private val dateProvider: DateProvider = mock()

    override fun getPresenterMocks() = arrayOf(
        reserveUseCase,
        getSportsActivityUseCase,
        savedReserveContactsUseCase,
        savedAgreementUseCase,
        addToFavoritesUseCase,
        removeFromFavoritesUseCase,
        planFavoriteRecordReminderUseCase
    )
    //</editor-fold>

    override fun setUp() {
        given(savedAgreementUseCase.setAgreementAccepted()).willReturn { Completable.complete() }
        instance.unbindView()
        (instance as ScheduleDetailsPresenter).clear()
        super.setUp()
    }

    @Test
    fun `bindView shows schedule WHEN view bound, has saved reserve contacts, has saved accepted agreement`() {
        val sportsActivity = createActivity()

        val reserveContacts = createReserveContacts()
        val isAgreementAccepted = true
        bindView(
            testData = TestData(
                sportsActivity = sportsActivity,
                reserveContacts = reserveContacts,
                isAgreementAccepted = isAgreementAccepted
            )
        )
    }

    @Test
    fun `bindView shows schedule WHEN view bound, has saved reserve contacts, has saved not accepted agreement`() {
        val sportsActivity = createActivity()

        val reserveContacts = createReserveContacts()
        val isAgreementAccepted = false
        bindView(
            testData = TestData(
                sportsActivity = sportsActivity,
                reserveContacts = reserveContacts,
                isAgreementAccepted = isAgreementAccepted
            )
        )
    }

    @Test
    fun `bindView shows schedule WHEN view bound, has no saved reserve contacts, has no saved agreement accepted`() {
        bindView(TestData())
    }

    @Test
    fun `bindView shows schedule with recordFrom WHEN view bound, recordFrom not passed`() {
        val now = Date(2020, 10, 28, 20, 0)
        given { dateProvider.getDate() }.willReturn(now)
        val sportsActivity = createActivity().let {
            it.copy(
                availableSlots = 1,
                schedule = it.schedule.copy(
                    totalSlots = 24,
                    recordFrom = now.add(hours = 1),
                    recordTo = now.add(hours = 10)
                )
            )
        }
        val sportsActivityDisplay = SportsActivityDisplay(
            preEntry = sportsActivity.schedule.preEntry,
            datetime = sportsActivity.schedule.datetime,
            group = sportsActivity.schedule.group,
            activity = sportsActivity.schedule.activity,
            recordingPeriod = "Доступно с 21:00 28 ноября",
            slotsCount = "1/24",
            hasAvailableSlots = true
        )
        bindView(
            TestData(
                sportsActivity = sportsActivity,
                sportsActivityDisplay = sportsActivityDisplay
            )
        )
        verify(dateProvider).getDate()
    }

    @Test
    fun `bindView shows schedule with recordTo WHEN view bound, recordFrom passed, recordTo not passed`() {
        val now = Date(2020, 10, 28, 21, 0)
        given { dateProvider.getDate() }.willReturn(now)
        val sportsActivity = createActivity().let {
            it.copy(
                availableSlots = 1,
                schedule = it.schedule.copy(
                    totalSlots = 24,
                    recordFrom = now.add(hours = -1),
                    recordTo = now.add(hours = 1)
                )
            )
        }
        val sportsActivityDisplay = SportsActivityDisplay(
            preEntry = sportsActivity.schedule.preEntry,
            datetime = sportsActivity.schedule.datetime,
            group = sportsActivity.schedule.group,
            activity = sportsActivity.schedule.activity,
            recordingPeriod = "Доступно до 22:00 28 ноября",
            slotsCount = "1/24",
            hasAvailableSlots = true
        )
        bindView(
            TestData(
                sportsActivity = sportsActivity,
                sportsActivityDisplay = sportsActivityDisplay
            )
        )
        verify(dateProvider).getDate()
    }

    @Test
    fun `unbindView WHEN view bound`() {
        val sportsActivity = createActivity()

        bindView(TestData(sportsActivity = sportsActivity))

        given(savedReserveContactsUseCase.saveReserveContacts(any()))
            .willReturn { Completable.complete() }
        given(savedAgreementUseCase.setAgreementAccepted())
            .willReturn { Completable.complete() }

        instance.unbindView().andTriggerActions()

        verify(view).getReserveContacts()
        verify(view).isAgreementAccepted()
    }

    @Test
    fun `unbindView WHEN view bound, reserve contacts inputed, agreement accepted`() {
        val sportsActivity = createActivity()

        bindView(TestData(sportsActivity = sportsActivity))

        val reserveContacts = createReserveContacts()
        given(view.getReserveContacts()).willReturn { reserveContacts }
        given(savedReserveContactsUseCase.saveReserveContacts(reserveContacts))
            .willReturn { Completable.complete() }
        given(savedAgreementUseCase.setAgreementAccepted())
            .willReturn { Completable.complete() }
        given(view.isAgreementAccepted()).willReturn { true }

        instance.unbindView().andTriggerActions()

        verify(view).getReserveContacts()
        verify(view).isAgreementAccepted()
        verify(savedReserveContactsUseCase).saveReserveContacts(reserveContacts)
        verify(savedAgreementUseCase).setAgreementAccepted()
    }

    @Test
    fun `onReserveClicked show success WHEN reserve success, view bound`() {
        val sportsActivity = createActivity()
        val reserveContacts = createReserveContacts()
        bindView(
            testData = TestData(
                sportsActivity = sportsActivity,
                reserveContacts = reserveContacts
            )
        )

        val (fio, phone) = reserveContacts
        given(reserveUseCase.reserve(sportsActivity, fio, phone, true))
            .willReturn { Single.just(ReserveResult.Success) }

        instance.onReserveClicked(true)
            .andTriggerActions()

        verify(getSportsActivityUseCase)
            .invoke(sportsActivity.schedule.clubId, sportsActivity.schedule.id)
        verify(view).getReserveContacts()
        verify(reserveUseCase).reserve(sportsActivity, fio, phone, true)
        verify(view).showSuccessReserved()
    }

    @Test
    fun `onReserveClicked show try later WHEN error, view bound`() {
        val sportsActivity = createActivity()
        val reserveContacts = createReserveContacts()
        bindView(
            TestData(
                sportsActivity = sportsActivity,
                reserveContacts = reserveContacts
            )
        )

        val (fio, phone) = reserveContacts
        val exception = RuntimeException(getRandomString())
        given(reserveUseCase.reserve(sportsActivity, fio, phone, true))
            .willReturn { Single.error(exception) }

        instance.onReserveClicked(true)
            .andTriggerActions()

        verify(getSportsActivityUseCase)
            .invoke(sportsActivity.schedule.clubId, sportsActivity.schedule.id)
        verify(view).getReserveContacts()
        verify(reserveUseCase).reserve(sportsActivity, fio, phone, true)
        verify(errorReporter).reportError(exception, "Ошибка при попытке записи на занятие")
        verify(view).showTryLater()
    }

    @Test
    fun `onReserveClicked show the time has gone WHEN the time has gone, view bound`() {
        val sportsActivity = createActivity()
        val reserveContacts = createReserveContacts()
        bindView(
            TestData(
                sportsActivity = sportsActivity,
                reserveContacts = reserveContacts
            )
        )

        val (fio, phone) = reserveContacts
        given(reserveUseCase.reserve(sportsActivity, fio, phone, true))
            .willReturn { Single.just(ReserveResult.TheTimeHasGone) }

        instance.onReserveClicked(true)
            .andTriggerActions()

        verify(getSportsActivityUseCase)
            .invoke(sportsActivity.schedule.clubId, sportsActivity.schedule.id)
        verify(view).getReserveContacts()
        verify(reserveUseCase).reserve(sportsActivity, fio, phone, true)
        verify(view).showTheTimeHasGone()
    }

    @Test
    fun `onReserveClicked show has no slots a priori WHEN a priori has no slots, view bound`() {
        val sportsActivity = createActivity()
        val reserveContacts = createReserveContacts()
        bindView(
            testData = TestData(
                sportsActivity = sportsActivity,
                reserveContacts = reserveContacts
            )
        )

        val (fio, phone) = reserveContacts
        given(reserveUseCase.reserve(sportsActivity, fio, phone, true))
            .willReturn { Single.just(ReserveResult.NoSlots.APriori) }

        instance.onReserveClicked(true)
            .andTriggerActions()

        verify(getSportsActivityUseCase)
            .invoke(sportsActivity.schedule.clubId, sportsActivity.schedule.id)
        verify(view).getReserveContacts()
        verify(reserveUseCase).reserve(sportsActivity, fio, phone, true)
        verify(view).showHasNoSlotsAPriori()
    }

    @Test
    fun `onReserveClicked show has no slots a posteriori WHEN a posteriori has no slots, view bound`() {
        val sportsActivity = createActivity()
        val reserveContacts = createReserveContacts()
        bindView(
            testData = TestData(
                sportsActivity = sportsActivity,
                reserveContacts = reserveContacts
            )
        )

        val (fio, phone) = reserveContacts
        given(reserveUseCase.reserve(sportsActivity, fio, phone, true))
            .willReturn { Single.just(ReserveResult.NoSlots.APosteriori) }

        instance.onReserveClicked(true)
            .andTriggerActions()

        verify(getSportsActivityUseCase)
            .invoke(sportsActivity.schedule.clubId, sportsActivity.schedule.id)
        verify(view).getReserveContacts()
        verify(reserveUseCase).reserve(sportsActivity, fio, phone, true)
        verify(view).showHasNoSlotsAPosteriori()
    }

    @Test
    fun `onReserveClicked show has already reserved WHEN already reserved, view bound`() {
        val sportsActivity = createActivity()
        val reserveContacts = createReserveContacts()
        bindView(
            testData = TestData(
                sportsActivity = sportsActivity,
                reserveContacts = reserveContacts
            )
        )

        val (fio, phone) = reserveContacts
        given(reserveUseCase.reserve(sportsActivity, fio, phone, true))
            .willReturn { Single.just(ReserveResult.AlreadyReserved) }

        instance.onReserveClicked(true)
            .andTriggerActions()

        verify(getSportsActivityUseCase)
            .invoke(sportsActivity.schedule.clubId, sportsActivity.schedule.id)
        verify(view).getReserveContacts()
        verify(reserveUseCase).reserve(sportsActivity, fio, phone, true)
        verify(view).showAlreadyReserved()
    }

    @Test
    fun `onReserveClicked show have to accept agreement WHEN not accepted agreement, view bound`() {
        val sportsActivity = createActivity()
        val reserveContacts = createReserveContacts()
        bindView(
            testData = TestData(
                sportsActivity = sportsActivity,
                reserveContacts = reserveContacts
            )
        )

        val (fio, phone) = reserveContacts
        given(reserveUseCase.reserve(sportsActivity, fio, phone, false))
            .willReturn { Single.just(ReserveResult.HaveToAcceptAgreement) }

        instance.onReserveClicked(false)
            .andTriggerActions()

        verify(getSportsActivityUseCase)
            .invoke(sportsActivity.schedule.clubId, sportsActivity.schedule.id)
        verify(view).getReserveContacts()
        verify(reserveUseCase).reserve(sportsActivity, fio, phone, false)
        verify(view).showHaveToAcceptAgreement()
    }

    @Test
    fun `onFavoriteClick show isFavorite true WHEN isFavorite was false, view bound`() {
        val sportsActivity = createActivity().copy(isFavorite = false)
        val reserveContacts = createReserveContacts()
        bindView(
            testData = TestData(
                sportsActivity = sportsActivity,
                reserveContacts = reserveContacts
            )
        )

        given(addToFavoritesUseCase.invoke(sportsActivity.schedule))
            .willReturn { Completable.complete() }
        given(planFavoriteRecordReminderUseCase.invoke(sportsActivity.schedule))
            .willReturn { Completable.complete() }

        instance.onFavoriteClick()
            .andTriggerActions()

        verify(getSportsActivityUseCase)
            .invoke(sportsActivity.schedule.clubId, sportsActivity.schedule.id)
        verify(addToFavoritesUseCase).invoke(sportsActivity.schedule)
        verify(planFavoriteRecordReminderUseCase).invoke(sportsActivity.schedule)
        verify(view).showIsFavorite(true)
    }

    @Test
    fun `onFavoriteClick show isFavorite false WHEN isFavorite was true, view bound`() {
        val sportsActivity = createActivity().copy(isFavorite = true)
        val reserveContacts = createReserveContacts()

        bindView(
            testData = TestData(
                sportsActivity = sportsActivity,
                reserveContacts = reserveContacts
            )
        )

        given(removeFromFavoritesUseCase.invoke(sportsActivity.schedule))
            .willReturn { Completable.complete() }

        instance.onFavoriteClick()
            .andTriggerActions()

        verify(getSportsActivityUseCase)
            .invoke(sportsActivity.schedule.clubId, sportsActivity.schedule.id)
        verify(removeFromFavoritesUseCase).invoke(sportsActivity.schedule)
        verify(view).showIsFavorite(false)
    }

    private fun bindView(testData: TestData) {
        bind(
            testData,
            prepare = {
                given(
                    getSportsActivityUseCase.invoke(
                        sportsActivity.schedule.clubId,
                        sportsActivity.schedule.id
                    )
                )
                    .willReturn { Single.just(sportsActivity) }
                given(savedReserveContactsUseCase.getReserveContacts())
                    .willReturn { reserveContacts.toMaybe() }
                given(savedAgreementUseCase.isAgreementAccepted())
                    .willReturn { isAgreementAccepted.toSingle() }

                instance.init(sportsActivity.schedule.id, sportsActivity.schedule.clubId)
            },
            verify = {
                verify(getSportsActivityUseCase)
                    .invoke(sportsActivity.schedule.clubId, sportsActivity.schedule.id)
                val actualSportsActivityDisplay =
                    view.capture { arg: SportsActivityDisplay -> showScheduleToReserve(arg) }
                assertThat(actualSportsActivityDisplay).isEqualTo(sportsActivityDisplay)
                verify(view).showIsFavorite(sportsActivity.isFavorite)
                verify(savedReserveContactsUseCase).getReserveContacts()
                verify(savedAgreementUseCase).isAgreementAccepted()
                reserveContacts?.let { verify(view).setReserveContacts(it) }
                if (isAgreementAccepted) verify(view).setAgreementAccepted()

                verifyNoMoreInteractions(view)
                reset(view)
                // and restore some setup after reset:
                //given(view.getSportsActivity()).willReturn(sportsActivity)
                given(view.getReserveContacts()).willReturn { reserveContacts }
            }
        )
    }

    private data class TestData(
        val sportsActivity: SportsActivity = createActivity(),
        val reserveContacts: ReserveContacts? = null,
        val isAgreementAccepted: Boolean = false,
        val sportsActivityDisplay: SportsActivityDisplay = sportsActivity.toDisplay(recordingPeriod = null)
    )
}

private fun createActivity() = createSportsActivity().let {
    it.copy(
        schedule = it.schedule.copy(
            preEntry = true,
            totalSlots = null,
            recordFrom = null,
            recordTo = null
        )
    )
}
