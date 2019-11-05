package ru.olegivo.afs.schedule.presentation

import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.reset
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.Test
import ru.olegivo.afs.BaseTestOf
import ru.olegivo.afs.extensions.toMaybe
import ru.olegivo.afs.favorites.domain.AddToFavoritesUseCase
import ru.olegivo.afs.helpers.getRandomString
import ru.olegivo.afs.schedule.domain.ReserveUseCase
import ru.olegivo.afs.schedule.domain.SavedReserveContactsUseCase
import ru.olegivo.afs.schedule.domain.models.ReserveContacts
import ru.olegivo.afs.schedule.domain.models.ReserveResult
import ru.olegivo.afs.schedules.domain.models.SportsActivity
import ru.olegivo.afs.schedules.domain.models.createReserveContacts
import ru.olegivo.afs.schedules.domain.models.createSportsActivity

class ScheduleDetailsPresenterTest : BaseTestOf<ScheduleDetailsContract.Presenter>() {
    override fun createInstance() = ScheduleDetailsPresenter(
        reserveUseCase,
        savedReserveContactsUseCase,
        addToFavoritesUseCase,
        schedulerRule.testScheduler
    )

    //<editor-fold desc="mocks">
    private val reserveUseCase: ReserveUseCase = mock()
    private val savedReserveContactsUseCase: SavedReserveContactsUseCase = mock()
    private val addToFavoritesUseCase: AddToFavoritesUseCase = mock()
    private val view: ScheduleDetailsContract.View = mock()

    override fun getAllMocks() = arrayOf(
        reserveUseCase,
        savedReserveContactsUseCase,
        addToFavoritesUseCase,
        view
    )
    //</editor-fold>

    override fun setUp() {
        instance.unbindView()
        super.setUp()
    }

    @Test
    fun `start shows schedule WHEN view bound, has saved reserve contacts`() {
        val sportsActivity = createSportsActivity()

        instance.bindView(view)
        val reserveContacts = createReserveContacts()
        start(sportsActivity, reserveContacts)

        verifyStart(sportsActivity, reserveContacts)
    }

    @Test
    fun `start shows schedule WHEN view bound, has no saved reserve contacts`() {
        val sportsActivity = createSportsActivity()

        instance.bindView(view)
        start(sportsActivity)

        verifyStart(sportsActivity)
    }

    @Test
    fun `saveReserveContacts WHEN no errors`() {
        val reserveContacts = createReserveContacts()
        given(savedReserveContactsUseCase.saveReserveContacts(reserveContacts))
            .willReturn(Completable.complete())

        instance.saveReserveContacts(reserveContacts)
            .andTriggerActions()

        verify(savedReserveContactsUseCase).saveReserveContacts(reserveContacts)
    }

    @Test
    fun `onReserveClicked show success WHEN reserve success, view bound`() {
        val sportsActivity = createSportsActivity()

        instance.bindView(view)
        val reserveContacts = createReserveContacts()
        start(sportsActivity, reserveContacts)

        val fio = getRandomString()
        val phone = getRandomString()
        given(reserveUseCase.reserve(sportsActivity, fio, phone))
            .willReturn(Single.just(ReserveResult.Success))

        instance.onReserveClicked(sportsActivity, fio, phone)
            .andTriggerActions()

        verifyStart(sportsActivity, reserveContacts)
        verify(reserveUseCase).reserve(sportsActivity, fio, phone)
        verify(view).showSuccessReserved()
    }

    @Test
    fun `onReserveClicked show try later WHEN error, view bound`() {
        val sportsActivity = createSportsActivity()

        instance.bindView(view)
        val reserveContacts = createReserveContacts()
        start(sportsActivity, reserveContacts)

        val fio = getRandomString()
        val phone = getRandomString()
        val exception = RuntimeException(getRandomString())
        given(reserveUseCase.reserve(sportsActivity, fio, phone))
            .willReturn(Single.error(exception))

        instance.onReserveClicked(sportsActivity, fio, phone)
            .andTriggerActions()

        verifyStart(sportsActivity, reserveContacts)
        verify(reserveUseCase).reserve(sportsActivity, fio, phone)
        verify(view).showTryLater()
    }

    @Test
    fun `onReserveClicked show the time has gone WHEN the time has gone, view bound`() {
        val sportsActivity = createSportsActivity()

        instance.bindView(view)
        val reserveContacts = createReserveContacts()
        start(sportsActivity, reserveContacts)

        val fio = getRandomString()
        val phone = getRandomString()
        given(reserveUseCase.reserve(sportsActivity, fio, phone))
            .willReturn(Single.just(ReserveResult.TheTimeHasGone))

        instance.onReserveClicked(sportsActivity, fio, phone)
            .andTriggerActions()

        verifyStart(sportsActivity, reserveContacts)
        verify(reserveUseCase).reserve(sportsActivity, fio, phone)
        verify(view).showTheTimeHasGone()
    }

    @Test
    fun `onReserveClicked show has no slots a priori WHEN a priori has no slots, view bound`() {
        val sportsActivity = createSportsActivity()

        instance.bindView(view)
        val reserveContacts = createReserveContacts()
        start(sportsActivity, reserveContacts)

        val fio = getRandomString()
        val phone = getRandomString()
        given(reserveUseCase.reserve(sportsActivity, fio, phone))
            .willReturn(Single.just(ReserveResult.NoSlots.APriori))

        instance.onReserveClicked(sportsActivity, fio, phone)
            .andTriggerActions()

        verifyStart(sportsActivity, reserveContacts)
        verify(reserveUseCase).reserve(sportsActivity, fio, phone)
        verify(view).showHasNoSlotsAPriori()
    }

    @Test
    fun `onReserveClicked show has no slots a posteriori WHEN a posteriori has no slots, view bound`() {
        val sportsActivity = createSportsActivity()

        instance.bindView(view)
        val reserveContacts = createReserveContacts()
        start(sportsActivity, reserveContacts)

        val fio = getRandomString()
        val phone = getRandomString()
        given(reserveUseCase.reserve(sportsActivity, fio, phone))
            .willReturn(Single.just(ReserveResult.NoSlots.APosteriori))

        instance.onReserveClicked(sportsActivity, fio, phone)
            .andTriggerActions()

        verifyStart(sportsActivity, reserveContacts)
        verify(reserveUseCase).reserve(sportsActivity, fio, phone)
        verify(view).showHasNoSlotsAPosteriori()
    }

    @Test
    fun `onReserveClicked show has already reserved WHEN already reserved, view bound`() {
        val sportsActivity = createSportsActivity()

        instance.bindView(view)
        val reserveContacts = createReserveContacts()
        start(sportsActivity, reserveContacts)

        val fio = getRandomString()
        val phone = getRandomString()
        given(reserveUseCase.reserve(sportsActivity, fio, phone))
            .willReturn(Single.just(ReserveResult.AlreadyReserved))

        instance.onReserveClicked(sportsActivity, fio, phone)
            .andTriggerActions()

        verifyStart(sportsActivity, reserveContacts)
        verify(reserveUseCase).reserve(sportsActivity, fio, phone)
        verify(view).showAlreadyReserved()
    }

    @Test
    fun `onFavoriteClick show isFavorite true WHEN isFavorite was false, view bound`() {
        val sportsActivity = createSportsActivity().copy(isFavorite = false)

        instance.bindView(view)
        val reserveContacts = createReserveContacts()
        start(sportsActivity, reserveContacts)
        verifyStart(sportsActivity, reserveContacts)
        verifyNoMoreInteractions(view)
        reset(view)

        given(addToFavoritesUseCase.invoke(sportsActivity.schedule))
            .willReturn(Completable.complete())

        instance.onFavoriteClick(sportsActivity.schedule, sportsActivity.isFavorite)
            .andTriggerActions()

        verify(addToFavoritesUseCase).invoke(sportsActivity.schedule)
        verify(view).showIsFavorite(true)
    }

    private fun verifyStart(sportsActivity: SportsActivity, reserveContacts: ReserveContacts? = null) {
        verify(view).showScheduleToReserve(sportsActivity)
        verify(view).showIsFavorite(sportsActivity.isFavorite)
        verify(savedReserveContactsUseCase).getReserveContacts()
        reserveContacts?.let { verify(view).setReserveContacts(it) }
    }

    private fun start(sportsActivity: SportsActivity, reserveContacts: ReserveContacts? = null) {
        given(savedReserveContactsUseCase.getReserveContacts())
            .willReturn(reserveContacts.toMaybe())
        instance.start(sportsActivity).andTriggerActions()
    }
}