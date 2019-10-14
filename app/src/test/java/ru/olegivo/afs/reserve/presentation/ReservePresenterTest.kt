package ru.olegivo.afs.reserve.presentation

import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.Single
import org.junit.Test
import ru.olegivo.afs.BaseTestOf
import ru.olegivo.afs.helpers.getRandomString
import ru.olegivo.afs.reserve.domain.ReserveUseCase
import ru.olegivo.afs.reserve.domain.models.ReserveResult
import ru.olegivo.afs.schedule.domain.models.createSchedule
import java.lang.RuntimeException

class ReservePresenterTest : BaseTestOf<ReserveContract.Presenter>() {
    override fun createInstance() = ReservePresenter(
        reserveUseCase,
        schedulerRule.testScheduler
    )

    //<editor-fold desc="mocks">
    private val reserveUseCase: ReserveUseCase = mock()
    private val view: ReserveContract.View = mock()

    override fun getAllMocks() = arrayOf<Any>(
        view
    )
    //</editor-fold>

    override fun setUp() {
        instance.unbindView()
        super.setUp()
    }

    @Test
    fun `start shows schedule WHEN view bound`() {
        val schedule = createSchedule()

        instance.bindView(view)
        instance.start(schedule)

        verify(view).showScheduleToReserve(schedule)
    }

    @Test
    fun `onReserveClicked show success WHEN reserve success, view bound`() {
        val schedule = createSchedule()

        instance.bindView(view)
        instance.start(schedule)

        val fio = getRandomString()
        val phone = getRandomString()
        given(reserveUseCase.reserve(schedule, fio, phone))
            .willReturn(Single.just(ReserveResult.Success))

        instance.onReserveClicked(schedule, fio, phone)
            .andTriggerActions()

        verify(view).showScheduleToReserve(schedule)
        verify(view).showSuccessReserved()
    }

    @Test
    fun `onReserveClicked show try later WHEN error, view bound`() {
        val schedule = createSchedule()

        instance.bindView(view)
        instance.start(schedule)

        val fio = getRandomString()
        val phone = getRandomString()
        val exception = RuntimeException(getRandomString())
        given(reserveUseCase.reserve(schedule, fio, phone))
            .willReturn(Single.error(exception))

        instance.onReserveClicked(schedule, fio, phone)
            .andTriggerActions()

        verify(view).showScheduleToReserve(schedule)
        verify(view).showTryLater()
    }

    @Test
    fun `onReserveClicked show the time has gone WHEN the time has gone, view bound`() {
        val schedule = createSchedule()

        instance.bindView(view)
        instance.start(schedule)

        val fio = getRandomString()
        val phone = getRandomString()
        given(reserveUseCase.reserve(schedule, fio, phone))
            .willReturn(Single.just(ReserveResult.TheTimeHasGone))

        instance.onReserveClicked(schedule, fio, phone)
            .andTriggerActions()

        verify(view).showScheduleToReserve(schedule)
        verify(view).showTheTimeHasGone()
    }

    @Test
    fun `onReserveClicked show has no slots a priori WHEN a priori has no slots, view bound`() {
        val schedule = createSchedule()

        instance.bindView(view)
        instance.start(schedule)

        val fio = getRandomString()
        val phone = getRandomString()
        given(reserveUseCase.reserve(schedule, fio, phone))
            .willReturn(Single.just(ReserveResult.NoSlots.APriori))

        instance.onReserveClicked(schedule, fio, phone)
            .andTriggerActions()

        verify(view).showScheduleToReserve(schedule)
        verify(view).showHasNoSlotsAPriori()
    }

    @Test
    fun `onReserveClicked show has no slots a posteriori WHEN a posteriori has no slots, view bound`() {
        val schedule = createSchedule()

        instance.bindView(view)
        instance.start(schedule)

        val fio = getRandomString()
        val phone = getRandomString()
        given(reserveUseCase.reserve(schedule, fio, phone))
            .willReturn(Single.just(ReserveResult.NoSlots.APosteriori))

        instance.onReserveClicked(schedule, fio, phone)
            .andTriggerActions()

        verify(view).showScheduleToReserve(schedule)
        verify(view).showHasNoSlotsAPosteriori()
    }
}