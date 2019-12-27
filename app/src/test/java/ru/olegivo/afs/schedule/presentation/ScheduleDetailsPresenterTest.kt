package ru.olegivo.afs.schedule.presentation

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.reset
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.willReturn
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.Test
import ru.olegivo.afs.BaseTestOf
import ru.olegivo.afs.common.presentation.Navigator
import ru.olegivo.afs.extensions.toMaybe
import ru.olegivo.afs.extensions.toSingle
import ru.olegivo.afs.favorites.domain.AddToFavoritesUseCase
import ru.olegivo.afs.favorites.domain.PlanFavoriteRecordReminderUseCase
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

class ScheduleDetailsPresenterTest : BaseTestOf<ScheduleDetailsContract.Presenter>() {
    override fun createInstance() = ScheduleDetailsPresenter(
        reserveUseCase,
        getSportsActivityUseCase,
        savedReserveContactsUseCase,
        savedAgreementUseCase,
        addToFavoritesUseCase,
        removeFromFavoritesUseCase,
        schedulerRule.testScheduler,
        navigator,
        planFavoriteRecordReminderUseCase
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

    private val view: ScheduleDetailsContract.View = mock()

    override fun getAllMocks() = arrayOf(
        reserveUseCase,
        getSportsActivityUseCase,
        savedReserveContactsUseCase,
        savedAgreementUseCase,
        addToFavoritesUseCase,
        removeFromFavoritesUseCase,
        planFavoriteRecordReminderUseCase,
        view
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
        val sportsActivity = createSportsActivity()

        val reserveContacts = createReserveContacts()
        val isAgreementAccepted = true
        bindView(sportsActivity, reserveContacts, isAgreementAccepted)

        verifyBindView(sportsActivity, reserveContacts, isAgreementAccepted)
    }

    @Test
    fun `bindView shows schedule WHEN view bound, has saved reserve contacts, has saved not accepted agreement`() {
        val sportsActivity = createSportsActivity()

        val reserveContacts = createReserveContacts()
        val isAgreementAccepted = false
        bindView(sportsActivity, reserveContacts, isAgreementAccepted)

        verifyBindView(sportsActivity, reserveContacts, isAgreementAccepted)
    }

    @Test
    fun `bindView shows schedule WHEN view bound, has no saved reserve contacts, has no saved agreement accepted`() {
        val sportsActivity = createSportsActivity()

        bindView(sportsActivity)

        verifyBindView(sportsActivity)
    }

    @Test
    fun `unbindView WHEN view bound`() {
        val sportsActivity = createSportsActivity()

        bindView(sportsActivity)
        verifyBindView(sportsActivity)

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
        val sportsActivity = createSportsActivity()

        bindView(sportsActivity)
        verifyBindView(sportsActivity)

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
        val sportsActivity = createSportsActivity()
        val reserveContacts = createReserveContacts()
        bindView(sportsActivity, reserveContacts)
        verifyBindView(sportsActivity, reserveContacts)

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
        val sportsActivity = createSportsActivity()
        val reserveContacts = createReserveContacts()
        bindView(sportsActivity, reserveContacts)
        verifyBindView(sportsActivity, reserveContacts)

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
        verify(view).showTryLater()
    }

    @Test
    fun `onReserveClicked show the time has gone WHEN the time has gone, view bound`() {
        val sportsActivity = createSportsActivity()
        val reserveContacts = createReserveContacts()
        bindView(sportsActivity, reserveContacts)
        verifyBindView(sportsActivity, reserveContacts)

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
        val sportsActivity = createSportsActivity()
        val reserveContacts = createReserveContacts()
        bindView(sportsActivity, reserveContacts)
        verifyBindView(sportsActivity, reserveContacts)

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
        val sportsActivity = createSportsActivity()
        val reserveContacts = createReserveContacts()
        bindView(sportsActivity, reserveContacts)
        verifyBindView(sportsActivity, reserveContacts)

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
        val sportsActivity = createSportsActivity()
        val reserveContacts = createReserveContacts()
        bindView(sportsActivity, reserveContacts)
        verifyBindView(sportsActivity, reserveContacts)

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
        val sportsActivity = createSportsActivity()
        val reserveContacts = createReserveContacts()
        bindView(sportsActivity, reserveContacts)
        verifyBindView(sportsActivity, reserveContacts)

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
        val sportsActivity = createSportsActivity().copy(isFavorite = false)
        val reserveContacts = createReserveContacts()
        bindView(sportsActivity, reserveContacts)
        verifyBindView(sportsActivity, reserveContacts)

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

    private fun bindView(
        sportsActivity: SportsActivity,
        reserveContacts: ReserveContacts? = null,
        isAgreementAccepted: Boolean = false
    ) {
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
        instance.bindView(view).andTriggerActions()
    }

    @Test
    fun `onFavoriteClick show isFavorite false WHEN isFavorite was true, view bound`() {
        val sportsActivity = createSportsActivity().copy(isFavorite = true)
        val reserveContacts = createReserveContacts()

        bindView(sportsActivity, reserveContacts)
        verifyBindView(sportsActivity, reserveContacts)

        given(removeFromFavoritesUseCase.invoke(sportsActivity.schedule))
            .willReturn { Completable.complete() }

        instance.onFavoriteClick()
            .andTriggerActions()

        verify(getSportsActivityUseCase)
            .invoke(sportsActivity.schedule.clubId, sportsActivity.schedule.id)
        verify(removeFromFavoritesUseCase).invoke(sportsActivity.schedule)
        verify(view).showIsFavorite(false)
    }

    private fun verifyBindView(
        sportsActivity: SportsActivity,
        reserveContacts: ReserveContacts? = null,
        isAgreementAccepted: Boolean = false
    ) {
        verify(getSportsActivityUseCase)
            .invoke(sportsActivity.schedule.clubId, sportsActivity.schedule.id)
        verify(view).showScheduleToReserve(sportsActivity)
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
}