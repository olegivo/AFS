package ru.olegivo.afs.schedule.presentation

import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy
import org.jetbrains.annotations.TestOnly
import ru.olegivo.afs.common.presentation.BasePresenter
import ru.olegivo.afs.common.presentation.BrowserDestination
import ru.olegivo.afs.common.presentation.Navigator
import ru.olegivo.afs.extensions.toMaybe
import ru.olegivo.afs.favorites.domain.AddToFavoritesUseCase
import ru.olegivo.afs.schedule.domain.GetSportsActivityUseCase
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
    private val getSportsActivity: GetSportsActivityUseCase,
    private val savedReserveContactsUseCase: SavedReserveContactsUseCase,
    private val savedAgreementUseCase: SavedAgreementUseCase,
    private val addToFavorites: AddToFavoritesUseCase,
    private val removeFromFavorites: RemoveFromFavoritesUseCase,
    @Named("main") private val mainScheduler: Scheduler,
    private val navigator: Navigator
) :
    BasePresenter<ScheduleDetailsContract.View>(),
    ScheduleDetailsContract.Presenter {

    private var sportsActivity: SportsActivity? = null
    private var clubId: Int = 0
    private var scheduleId: Long = 0
    private var isFavorite: Boolean = false

    override fun init(scheduleId: Long, clubId: Int) {
        this.scheduleId = scheduleId
        this.clubId = clubId
    }

    @TestOnly
    fun clear() {
        scheduleId = 0
        clubId = 0
        sportsActivity = null
    }

    override fun bindView(view: ScheduleDetailsContract.View) {
        super.bindView(view)
        start()
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
        reserveUseCase.reserve(sportsActivity!!, fio, phone, hasAcceptedAgreement)
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
        val sportsActivity = sportsActivity!!

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
                {
                    view?.showIsFavorite(!isFavorite)
                    this.sportsActivity = sportsActivity.copy(isFavorite = !isFavorite)
                },
                { onError(it, errorMessage) }
            )
            .addToComposite()
    }

    private fun start() {
        sportsActivity.toMaybe()
            .switchIfEmpty(
                Single.defer { getSportsActivity(clubId, scheduleId) }
                    .doOnSuccess {
                        sportsActivity = it
                        isFavorite = it.isFavorite
                    }
            )
            .observeOn(mainScheduler)
            .subscribeBy(
                onSuccess = { sportsActivity ->
                    view?.showScheduleToReserve(sportsActivity)
                    view?.showIsFavorite(isFavorite)
                },
                onError = {
                    onError(it, "Ошибка при получении занятия")
                }
            )
            .addToComposite()

        savedReserveContactsUseCase.getReserveContacts()
            .observeOn(mainScheduler)
            .subscribeBy(
                onSuccess = { view?.setReserveContacts(it) },
                onError = {
                    onError(it, "Ошибка при восстановлении контактов для записи на занятие")
                })
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