package ru.olegivo.afs.schedule.presentation

import io.reactivex.Completable
import io.reactivex.Scheduler
import ru.olegivo.afs.common.presentation.BasePresenter
import ru.olegivo.afs.common.presentation.BrowserDestination
import ru.olegivo.afs.common.presentation.Navigator
import ru.olegivo.afs.favorites.domain.AddToFavoritesUseCase
import ru.olegivo.afs.schedule.domain.RemoveFromFavoritesUseCase
import ru.olegivo.afs.schedule.domain.ReserveUseCase
import ru.olegivo.afs.schedule.domain.SavedAgreementUseCase
import ru.olegivo.afs.schedule.domain.SavedReserveContactsUseCase
import ru.olegivo.afs.schedule.domain.models.ReserveContacts
import ru.olegivo.afs.schedule.domain.models.ReserveResult
import ru.olegivo.afs.schedules.domain.models.SportsActivity
import javax.inject.Inject
import javax.inject.Named

class ScheduleDetailsPresenter @Inject constructor(
    private val reserveUseCase: ReserveUseCase,
    private val savedReserveContactsUseCase: SavedReserveContactsUseCase,
    private val savedAgreementUseCase: SavedAgreementUseCase,
    private val addToFavorites: AddToFavoritesUseCase,
    private val removeFromFavorites: RemoveFromFavoritesUseCase,
    @Named("main") private val mainScheduler: Scheduler,
    private val navigator: Navigator
) :
    BasePresenter<ScheduleDetailsContract.View>(),
    ScheduleDetailsContract.Presenter {

    private var isFavorite: Boolean = false

    override fun bindView(view: ScheduleDetailsContract.View) {
        super.bindView(view)
        start(getSportsActivity())
    }

    override fun unbindView() {
        view?.let {
            it.getReserveContacts()?.let { reserveContacts -> saveReserveContacts(reserveContacts) }
            if (it.isAgreementAccepted()) setAgreementAccepted()
        }
        super.unbindView()
    }

    override fun onReserveClicked(
        hasAcceptedAgreement: Boolean
    ) {
        val (fio, phone) = view!!.getReserveContacts()!!
        val sportsActivity = getSportsActivity()
        reserveUseCase.reserve(sportsActivity, fio, phone, hasAcceptedAgreement)
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
                        ReserveResult.HaveToAcceptAgreement -> view?.showHaveToAcceptAgreement()
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

    override fun onAgreementClicked() {
        navigator.navigateTo(BrowserDestination("http://static.mobifitness.ru/Privacy/privacy.html"))
    }

    override fun onFavoriteClick() {
        val sportsActivity = getSportsActivity()

        val action: Completable
        val errorMessage: String

        val isFavorite = sportsActivity.isFavorite
        if (isFavorite) {
            action = removeFromFavorites(sportsActivity.schedule)
            errorMessage = "Ошибка при удалении из избранного"
        } else {
            action = addToFavorites(sportsActivity.schedule)
            errorMessage = "Ошибка при добавлении в избранное"
        }

        action
            .observeOn(mainScheduler)
            .subscribe(
                { view?.setIsFavorite(!isFavorite) },
                { onError(it, errorMessage) }
            )
            .addToComposite()
    }

    private fun getSportsActivity() =
        this.view?.getSportsActivity()!! // TODO: create new Use Case and get it from there

    private fun start(sportsActivity: SportsActivity) {
        view?.showScheduleToReserve(sportsActivity)
        isFavorite = sportsActivity.isFavorite
        view?.setIsFavorite(isFavorite)
        savedReserveContactsUseCase.getReserveContacts()
            .observeOn(mainScheduler)
            .subscribe(
                { view?.setReserveContacts(it) },
                { onError(it, "Ошибка при восстановлении контактов для записи на занятие") })
            .addToComposite()

        savedAgreementUseCase.isAgreementAccepted()
            .observeOn(mainScheduler)
            .subscribe(
                { isAgreementAccepted -> if (isAgreementAccepted) view?.setAgreementAccepted() },
                {
                    onError(
                        it,
                        "Ошибка при получении факта принятия соглашения обработки персональных данных"
                    )
                })
            .addToComposite()
    }

    private fun saveReserveContacts(reserveContacts: ReserveContacts) {
        savedReserveContactsUseCase.saveReserveContacts(reserveContacts)
            .subscribe(
                {},
                { onError(it, "Ошибка при сохранении контактов для записи на занятие") }
            )
            .addToComposite()
    }

    private fun setAgreementAccepted() {
        savedAgreementUseCase.setAgreementAccepted()
            .subscribe(
                {},
                {
                    onError(
                        it,
                        "Ошибка при сохранении факта принятия соглашения обработки персональных данных"
                    )
                }
            )
            .addToComposite()
    }

}