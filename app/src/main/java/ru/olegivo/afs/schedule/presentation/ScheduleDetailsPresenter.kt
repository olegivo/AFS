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

import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy
import org.jetbrains.annotations.TestOnly
import ru.olegivo.afs.analytics.domain.AnalyticsProvider
import ru.olegivo.afs.common.domain.DateProvider
import ru.olegivo.afs.common.domain.ErrorReporter
import ru.olegivo.afs.common.presentation.BasePresenter
import ru.olegivo.afs.common.presentation.BrowserDestination
import ru.olegivo.afs.common.presentation.Navigator
import ru.olegivo.afs.extensions.toMaybe
import ru.olegivo.afs.favorites.domain.AddToFavoritesUseCase
import ru.olegivo.afs.favorites.domain.PlanFavoriteRecordReminderUseCase
import ru.olegivo.afs.schedule.analytics.ScheduleDetailsAnalytic
import ru.olegivo.afs.schedule.domain.GetSportsActivityUseCase
import ru.olegivo.afs.schedule.domain.RemoveFromFavoritesUseCase
import ru.olegivo.afs.schedule.domain.ReserveUseCase
import ru.olegivo.afs.schedule.domain.SavedAgreementUseCase
import ru.olegivo.afs.schedule.domain.SavedReserveContactsUseCase
import ru.olegivo.afs.schedule.domain.models.ReserveContacts
import ru.olegivo.afs.schedule.domain.models.ReserveResult
import ru.olegivo.afs.schedules.domain.models.Schedule
import ru.olegivo.afs.schedules.domain.models.SportsActivity
import ru.olegivo.afs.schedules.presentation.models.SportsActivityDisplay
import ru.olegivo.afs.schedules.presentation.models.toDisplay
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject
import javax.inject.Named

class ScheduleDetailsPresenter
@Suppress("LongParameterList")
@Inject
constructor(
    private val reserveUseCase: ReserveUseCase,
    private val getSportsActivity: GetSportsActivityUseCase,
    private val savedReserveContactsUseCase: SavedReserveContactsUseCase,
    private val savedAgreementUseCase: SavedAgreementUseCase,
    private val addToFavorites: AddToFavoritesUseCase,
    private val removeFromFavorites: RemoveFromFavoritesUseCase,
    @Named("main") private val mainScheduler: Scheduler,
    private val navigator: Navigator,
    private val planFavoriteRecordReminderUseCase: PlanFavoriteRecordReminderUseCase,
    private val locale: Locale,
    private val dateProvider: DateProvider,
    errorReporter: ErrorReporter,
    analyticsProvider: AnalyticsProvider
) :
    BasePresenter<ScheduleDetailsContract.View>(errorReporter, analyticsProvider),
    ScheduleDetailsContract.Presenter {

    private val dateTimeFormat: SimpleDateFormat by lazy {
        SimpleDateFormat("HH:mm dd MMMM", locale)
    }

    private var sportsActivity: SportsActivity? = null
    private var sportsActivityDisplay: SportsActivityDisplay? = null
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
        logEvent(ScheduleDetailsAnalytic.Screens.ScheduleDetails.OnReserveClicked)
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
                    view?.showTryLater()
                    onError(t, "Ошибка при попытке записи на занятие")
                }
            )
            .addToComposite()
    }

    override fun onAgreementClicked() {
        logEvent(ScheduleDetailsAnalytic.Screens.ScheduleDetails.OnViewAgreementClicked)
        navigator.navigateTo(BrowserDestination("http://static.mobifitness.ru/Privacy/privacy.html"))
    }

    override fun onFavoriteClick() {
        val sportsActivity = sportsActivity!!

        val action: Completable
        val errorMessage: String

        val isFavorite = sportsActivity.isFavorite
        if (isFavorite) {
            action = removeFromFavorites(sportsActivity.schedule)
                .startWith(
                    logEventCompletable(ScheduleDetailsAnalytic.Screens.ScheduleDetails.OnRemoveFromFavoritesClicked)
                )
            errorMessage = "Ошибка при удалении из избранного"
        } else {
            action = addToFavorites(sportsActivity.schedule)
                .startWith(
                    logEventCompletable(ScheduleDetailsAnalytic.Screens.ScheduleDetails.OnAddToFavoritesClicked)
                )
            errorMessage = "Ошибка при добавлении в избранное"
        }

        action
            .observeOn(mainScheduler)
            .subscribe(
                {
                    view?.showIsFavorite(!isFavorite)
                    this.sportsActivity = sportsActivity.copy(isFavorite = !isFavorite)
                    if (!isFavorite) {
                        planFavoriteRecordReminderUseCase(sportsActivity.schedule)
                            .observeOn(mainScheduler)
                            .subscribeBy(
                                onError = {
                                    onError(it, "Ошибка при планировании уведомления")
                                }
                            )
                            .addToComposite()
                    }
                },
                { onError(it, errorMessage) }
            )
            .addToComposite()
    }

    private fun start() {
        sportsActivityDisplay.toMaybe()
            .switchIfEmpty(
                Single.defer { getSportsActivity(clubId, scheduleId) }
                    .doOnSuccess {
                        sportsActivity = it
                        isFavorite = it.isFavorite
                    }
                    .map {
                        it.toDisplay(
                            datetime = dateTimeFormat.format(it.schedule.datetime),
                            recordingPeriod = it.schedule.getRecordingPeriod()
                        )
                    }
                    .doOnSuccess { sportsActivityDisplay = it }
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
                }
            )
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
                }
            )
            .addToComposite()
    }

    private fun Schedule.getRecordingPeriod() =
        recordTo?.let { recordTo ->
            val now = dateProvider.getDate()

            if (recordTo > now) {
                recordFrom?.let { recordFrom ->
                    if (recordFrom > now) {
                        "Доступно с ${dateTimeFormat.format(recordFrom)}"
                    } else {
                        null
                    }
                }
                    ?: "Доступно до ${dateTimeFormat.format(recordTo)}"
            } else {
                "Закончилась"
            }
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
        logEventCompletable(ScheduleDetailsAnalytic.Screens.ScheduleDetails.SaveAgreementAccepted)
            .andThen(savedAgreementUseCase.setAgreementAccepted())
            .subscribeBy(
                onError = {
                    onError(
                        it,
                        "Ошибка при сохранении факта принятия соглашения обработки персональных данных"
                    )
                }
            )
            .addToComposite()
    }
}
