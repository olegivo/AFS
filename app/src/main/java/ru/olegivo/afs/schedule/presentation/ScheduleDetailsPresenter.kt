package ru.olegivo.afs.schedule.presentation

import io.reactivex.Scheduler
import ru.olegivo.afs.common.presentation.BasePresenter
import ru.olegivo.afs.favorites.domain.AddToFavoritesUseCase
import ru.olegivo.afs.schedule.domain.ReserveUseCase
import ru.olegivo.afs.schedule.domain.SavedReserveContactsUseCase
import ru.olegivo.afs.schedule.domain.models.ReserveContacts
import ru.olegivo.afs.schedule.domain.models.ReserveResult
import ru.olegivo.afs.schedules.domain.models.Schedule
import ru.olegivo.afs.schedules.domain.models.SportsActivity
import javax.inject.Inject
import javax.inject.Named

class ScheduleDetailsPresenter @Inject constructor(
    private val reserveUseCase: ReserveUseCase,
    private val savedReserveContactsUseCase: SavedReserveContactsUseCase,
    private val addToFavorites: AddToFavoritesUseCase,
    @Named("main") private val mainScheduler: Scheduler
) :
    BasePresenter<ScheduleDetailsContract.View>(),
    ScheduleDetailsContract.Presenter {

    override fun start(sportsActivity: SportsActivity) {
        view?.showScheduleToReserve(sportsActivity)
        view?.showIsFavorite(sportsActivity.isFavorite)
        savedReserveContactsUseCase.getReserveContacts()
            .observeOn(mainScheduler)
            .subscribe(
                { view?.setReserveContacts(it) },
                { onError(it, "Ошибка при восстановлении контактов для записи на занятие") })
            .addToComposite()
    }

    override fun saveReserveContacts(reserveContacts: ReserveContacts) {
        savedReserveContactsUseCase.saveReserveContacts(reserveContacts)
            .subscribe(
                {},
                { onError(it, "Ошибка при сохранении контактов для записи на занятие") }
            )
            .addToComposite()
    }

    override fun onReserveClicked(sportsActivity: SportsActivity, fio: String, phone: String) {
        reserveUseCase.reserve(sportsActivity, fio, phone)
            .observeOn(mainScheduler)
            .subscribe(
                { reserveResult ->
                    when (reserveResult) {
                        ReserveResult.Success -> view?.showSuccessReserved()
                        ReserveResult.TheTimeHasGone -> view?.showTheTimeHasGone()
                        ReserveResult.NoSlots.APriori -> view?.showHasNoSlotsAPriori()
                        ReserveResult.NoSlots.APosteriori -> view?.showHasNoSlotsAPosteriori()
                        ReserveResult.NameAndPhoneShouldBeStated -> view?.showNameAndPhoneShouldBeStated()
                        ReserveResult.AlreadyReserved -> view?.showAlreadyReserved()
                    }
                },
                { t ->
                    t.printStackTrace()
                    view?.showTryLater()
                    onError(t, "Ошибка при попытке записи на занятие")
                }
            )
            .addToComposite()
    }

    override fun onFavoriteClick(
        schedule: Schedule,
        isFavorite: Boolean
    ) {
        if (!isFavorite) {
            addToFavorites(schedule)
                .observeOn(mainScheduler)
                .subscribe(
                    { view?.showIsFavorite(!isFavorite) },
                    { onError(it, "Ошибка при изменении избранного") }
                )
                .addToComposite()
        }
    }

}