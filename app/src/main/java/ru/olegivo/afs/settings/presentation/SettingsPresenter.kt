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

package ru.olegivo.afs.settings.presentation

import androidx.annotation.VisibleForTesting
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy
import ru.olegivo.afs.analytics.domain.AnalyticsProvider
import ru.olegivo.afs.clubs.domain.GetClubsUseCase
import ru.olegivo.afs.clubs.domain.GetCurrentClubUseCase
import ru.olegivo.afs.clubs.domain.SetCurrentClubUseCase
import ru.olegivo.afs.clubs.domain.models.Club
import ru.olegivo.afs.common.domain.ErrorReporter
import ru.olegivo.afs.common.presentation.BasePresenter
import ru.olegivo.afs.extensions.toMaybe
import ru.olegivo.afs.schedule.domain.ReserveRepository
import ru.olegivo.afs.settings.domain.DeleteDatabaseUseCase
import javax.inject.Inject
import javax.inject.Named

class SettingsPresenter @Inject constructor(
    private val reserveRepository: ReserveRepository,
    private val getClubs: GetClubsUseCase,
    private val getCurrentClub: GetCurrentClubUseCase,
    private val setCurrentClub: SetCurrentClubUseCase,
    private val deleteDatabase: DeleteDatabaseUseCase,
    @Named("main") private val mainScheduler: Scheduler,
    errorReporter: ErrorReporter,
    analyticsProvider: AnalyticsProvider
) : BasePresenter<SettingsContract.View>(errorReporter, analyticsProvider),
    SettingsContract.Presenter {

    private var isStubReserve: Boolean? = null

    override fun bindView(view: SettingsContract.View) {
        super.bindView(view)

        isStubReserve.toMaybe()
            .switchIfEmpty(Single.defer { reserveRepository.isStubReserve() })
            .observeOn(mainScheduler)
            .subscribeBy(
                onSuccess = { isStubReserve ->
                    this.isStubReserve = isStubReserve
                    this.view?.enableIsStubReserveCheckBox(isStubReserve)
                },
                onError = { onError(it, "Failed to fetch stub reserve") }
            )
            .addToComposite()
    }

    override fun onStubReserveChecked(isChecked: Boolean) {
        reserveRepository.setStubReserve(isChecked)
            .observeOn(mainScheduler)
            .doOnSubscribe { view?.disableIsStubReserveCheckBox() }
            .doFinally { view?.enableIsStubReserveCheckBox(isStubReserve ?: false) }
            .subscribeBy(onError = { onError(it, "Failed to set stub reserve") })
            .addToComposite()
    }

    override fun onDropDbBClicked() {
        deleteDatabase()
            .observeOn(mainScheduler)
            .subscribeBy(
                onComplete = { view?.showMessage("БД удалена") },
                onError = { onError(it, "Failed to drop database") }
            )
            .addToComposite()
    }

    override fun onChooseClubClicked() {
        getClubs()
            .flatMap { clubs ->
                getCurrentClub().toSingle(-1)
                    .map { currentClubId -> clubs to currentClubId }
            }
            .observeOn(mainScheduler)
            .subscribeBy(
                onSuccess = { info ->
                    val clubs = info.first
                    val selectedClub = clubs.singleOrNull {
                        val currentClubId = info.second
                        it.id == currentClubId
                    }
                    view?.showChooseClubDialog(clubs, selectedClub)
                },
                onError = { onError(it, "Failed to fetch clubs") }
            )
            .addToComposite()
    }

    override fun onSetDefaultClubClicked() {
        setCurrentClub(DefaultClubId)
            .observeOn(mainScheduler)
            .subscribeBy(
                onComplete = { view?.showMessage("Выбран клуб по умолчанию") },
                onError = { onError(it, "Failed to set default club") }
            )
            .addToComposite()
    }

    override fun onCurrentClubSelected(club: Club) {
        setCurrentClub(club.id)
            .observeOn(mainScheduler)
            .subscribeBy(
                onComplete = { view?.showMessage("Current club is ${club.title}") },
                onError = { onError(it, "Failed to fetch current club") }
            )
            .addToComposite()
    }

    @VisibleForTesting
    fun reset() {
        isStubReserve = null
    }

    companion object {
        const val DefaultClubId = 375
    }
}
