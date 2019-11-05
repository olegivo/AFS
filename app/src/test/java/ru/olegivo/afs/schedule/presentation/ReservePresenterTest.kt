package ru.olegivo.afs.schedule.presentation

import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.Test
import ru.olegivo.afs.BaseTestOf
import ru.olegivo.afs.extensions.toMaybe
import ru.olegivo.afs.helpers.getRandomString
import ru.olegivo.afs.schedule.domain.ReserveUseCase
import ru.olegivo.afs.schedule.domain.SavedReserveContactsUseCase
import ru.olegivo.afs.schedule.domain.models.ReserveContacts
import ru.olegivo.afs.schedule.domain.models.ReserveResult
import ru.olegivo.afs.schedules.domain.models.Schedule
import ru.olegivo.afs.schedules.domain.models.createReserveContacts
import ru.olegivo.afs.schedules.domain.models.createSchedule

class ReservePresenterTest : BaseTestOf<ReserveContract.Presenter>() {
    override fun createInstance() = ReservePresenter(
        reserveUseCase,
        savedReserveContactsUseCase,
        schedulerRule.testScheduler
    )

    //<editor-fold desc="mocks">
    private val reserveUseCase: ReserveUseCase = mock()
    private val savedReserveContactsUseCase: SavedReserveContactsUseCase = mock()
    private val view: ReserveContract.View = mock()

    override fun getAllMocks() = arrayOf(
        reserveUseCase,
        savedReserveContactsUseCase,
        view
    )
    //</editor-fold>

    override fun setUp() {
        instance.unbindView()
        super.setUp()
    }

    @Test
    fun `start shows schedule WHEN view bound, has saved reserve contacts`() {
        val schedule = createSchedule()

        instance.bindView(view)
        val reserveContacts = createReserveContacts()
        start(schedule, reserveContacts)

        verifyStart(schedule, reserveContacts)
    }

    @Test
    fun `start shows schedule WHEN view bound, has no saved reserve contacts`() {
        val schedule = createSchedule()

        instance.bindView(view)
        start(schedule)

        verifyStart(schedule)
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
        val schedule = createSchedule()

        instance.bindView(view)
        val reserveContacts = createReserveContacts()
        start(schedule, reserveContacts)

        val fio = getRandomString()
        val phone = getRandomString()
        given(reserveUseCase.reserve(schedule, fio, phone))
            .willReturn(Single.just(ReserveResult.Success))

        instance.onReserveClicked(schedule, fio, phone)
            .andTriggerActions()

        verifyStart(schedule, reserveContacts)
        verify(reserveUseCase).reserve(schedule, fio, phone)
        verify(view).showSuccessReserved()
    }

    @Test
    fun `onReserveClicked show try later WHEN error, view bound`() {
        val schedule = createSchedule()

        instance.bindView(view)
        val reserveContacts = createReserveContacts()
        start(schedule, reserveContacts)

        val fio = getRandomString()
        val phone = getRandomString()
        val exception = RuntimeException(getRandomString())
        given(reserveUseCase.reserve(schedule, fio, phone))
            .willReturn(Single.error(exception))

        instance.onReserveClicked(schedule, fio, phone)
            .andTriggerActions()

        verifyStart(schedule, reserveContacts)
        verify(reserveUseCase).reserve(schedule, fio, phone)
        verify(view).showTryLater()
    }

    @Test
    fun `onReserveClicked show the time has gone WHEN the time has gone, view bound`() {
        val schedule = createSchedule()

        instance.bindView(view)
        val reserveContacts = createReserveContacts()
        start(schedule, reserveContacts)

        val fio = getRandomString()
        val phone = getRandomString()
        given(reserveUseCase.reserve(schedule, fio, phone))
            .willReturn(Single.just(ReserveResult.TheTimeHasGone))

        instance.onReserveClicked(schedule, fio, phone)
            .andTriggerActions()

        verifyStart(schedule, reserveContacts)
        verify(reserveUseCase).reserve(schedule, fio, phone)
        verify(view).showTheTimeHasGone()
    }

    @Test
    fun `onReserveClicked show has no slots a priori WHEN a priori has no slots, view bound`() {
        val schedule = createSchedule()

        instance.bindView(view)
        val reserveContacts = createReserveContacts()
        start(schedule, reserveContacts)

        val fio = getRandomString()
        val phone = getRandomString()
        given(reserveUseCase.reserve(schedule, fio, phone))
            .willReturn(Single.just(ReserveResult.NoSlots.APriori))

        instance.onReserveClicked(schedule, fio, phone)
            .andTriggerActions()

        verifyStart(schedule, reserveContacts)
        verify(reserveUseCase).reserve(schedule, fio, phone)
        verify(view).showHasNoSlotsAPriori()
    }

    @Test
    fun `onReserveClicked show has no slots a posteriori WHEN a posteriori has no slots, view bound`() {
        val schedule = createSchedule()

        instance.bindView(view)
        val reserveContacts = createReserveContacts()
        start(schedule, reserveContacts)

        val fio = getRandomString()
        val phone = getRandomString()
        given(reserveUseCase.reserve(schedule, fio, phone))
            .willReturn(Single.just(ReserveResult.NoSlots.APosteriori))

        instance.onReserveClicked(schedule, fio, phone)
            .andTriggerActions()

        verifyStart(schedule, reserveContacts)
        verify(reserveUseCase).reserve(schedule, fio, phone)
        verify(view).showHasNoSlotsAPosteriori()
    }


    @Test
    fun `onReserveClicked show has already reserved WHEN already reserved, view bound`() {
        val schedule = createSchedule()

        instance.bindView(view)
        val reserveContacts = createReserveContacts()
        start(schedule, reserveContacts)

        val fio = getRandomString()
        val phone = getRandomString()
        given(reserveUseCase.reserve(schedule, fio, phone))
            .willReturn(Single.just(ReserveResult.AlreadyReserved))

        instance.onReserveClicked(schedule, fio, phone)
            .andTriggerActions()

        verifyStart(schedule, reserveContacts)
        verify(reserveUseCase).reserve(schedule, fio, phone)
        verify(view).showAlreadyReserved()
    }

    private fun verifyStart(schedule: Schedule, reserveContacts: ReserveContacts? = null) {
        verify(view).showScheduleToReserve(schedule)
        verify(savedReserveContactsUseCase).getReserveContacts()
        reserveContacts?.let { verify(view).setReserveContacts(it) }
    }

    private fun start(schedule: Schedule, reserveContacts: ReserveContacts? = null) {
        given(savedReserveContactsUseCase.getReserveContacts())
            .willReturn(reserveContacts.toMaybe())
        instance.start(schedule).andTriggerActions()
    }
}