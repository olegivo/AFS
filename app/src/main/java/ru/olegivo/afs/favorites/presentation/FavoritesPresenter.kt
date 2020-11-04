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

package ru.olegivo.afs.favorites.presentation

import io.reactivex.Scheduler
import io.reactivex.rxkotlin.subscribeBy
import ru.olegivo.afs.analytics.domain.AnalyticsProvider
import ru.olegivo.afs.common.domain.ErrorReporter
import ru.olegivo.afs.common.presentation.BasePresenter
import ru.olegivo.afs.favorites.domain.GetFavoritesUseCase
import ru.olegivo.afs.favorites.presentation.models.FavoritesItem
import javax.inject.Inject
import javax.inject.Named

class FavoritesPresenter @Inject constructor(
    private val getFavorites: GetFavoritesUseCase,
    @Named("main") private val mainScheduler: Scheduler,
    errorReporter: ErrorReporter,
    analyticsProvider: AnalyticsProvider
) :
    BasePresenter<FavoritesContract.View>(errorReporter, analyticsProvider),
    FavoritesContract.Presenter {

    override fun bindView(view: FavoritesContract.View) {
        super.bindView(view)
        start()
    }

    private fun start() {
        getFavorites()
            .observeOn(mainScheduler)
            .doOnSubscribe { view?.showProgress() }
            .doFinally { view?.hideProgress() }
            .subscribeBy(
                onSuccess = this::showResult,
                onError = {
                    onError(it, "Ошибка при получении списка избранных занятий")
                }
            )
            .addToComposite()
    }

    private fun showResult(favorites: List<FavoritesItem>) {
        view?.showFavorites(favorites)
    }
}
