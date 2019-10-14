package ru.olegivo.afs.reserve.presentation

import io.reactivex.Scheduler
import ru.olegivo.afs.common.presentation.BasePresenter
import ru.olegivo.afs.reserve.domain.ReserveUseCase
import ru.olegivo.afs.reserve.domain.models.ReserveResult
import ru.olegivo.afs.schedule.domain.models.Schedule
import javax.inject.Inject
import javax.inject.Named

class ReservePresenter @Inject constructor(private val reserveUseCase: ReserveUseCase, @Named("main") private val mainScheduler: Scheduler) :
    BasePresenter<ReserveContract.View>(),
    ReserveContract.Presenter {

    override fun start(schedule: Schedule) {
        view?.showScheduleToReserve(schedule)
    }

    override fun onReserveClicked(schedule: Schedule, fio: String, phone: String) {
        reserveUseCase.reserve(schedule, fio, phone)
            .observeOn(mainScheduler)
            .subscribe(
                { reserveResult ->
                    when (reserveResult) {
                        ReserveResult.Success -> view?.showSuccessReserved()
                        ReserveResult.TheTimeHasGone -> view?.showTheTimeHasGone()
                        ReserveResult.NoSlots.APriori -> view?.showHasNoSlotsAPriori()
                        ReserveResult.NoSlots.APosteriori -> view?.showHasNoSlotsAPosteriori()
                        ReserveResult.NameAndPhoneShouldBeStated -> view?.showNameAndPhoneShouldBeStated()
                    }
                },
                { t ->
                    t.printStackTrace()
                    view?.showTryLater()
                }
            )
            .addToComposite()
    }
}