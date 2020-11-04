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

package ru.olegivo.afs.schedules.presentation

import io.reactivex.Scheduler
import io.reactivex.rxkotlin.subscribeBy
import ru.olegivo.afs.analytics.domain.AnalyticsProvider
import ru.olegivo.afs.common.domain.ErrorReporter
import ru.olegivo.afs.common.presentation.BasePresenter
import ru.olegivo.afs.common.presentation.Navigator
import ru.olegivo.afs.schedule.presentation.models.ReserveDestination
import ru.olegivo.afs.schedules.analytics.SchedulesAnalytic
import ru.olegivo.afs.schedules.domain.ActualizeScheduleUseCase
import ru.olegivo.afs.schedules.domain.GetDaySportsActivitiesUseCase
import ru.olegivo.afs.schedules.domain.models.SportsActivity
import java.util.*
import javax.inject.Inject
import javax.inject.Named

class DaySchedulePresenter @Inject constructor(
    private val getDaySportsActivities: GetDaySportsActivitiesUseCase,
    private val actualizeSchedule: ActualizeScheduleUseCase,
    private val navigator: Navigator,
    @Named("main") private val mainScheduler: Scheduler,
    errorReporter: ErrorReporter,
    analyticsProvider: AnalyticsProvider
) : BasePresenter<DayScheduleContract.View>(errorReporter, analyticsProvider),
    DayScheduleContract.Presenter {

    override fun bindView(view: DayScheduleContract.View) {
        super.bindView(view)
        start(view.clubId, view.day)
    }

    override fun actualizeSchedule() {
        actualizeSchedule.invoke(view!!.clubId)
            .observeOn(mainScheduler)
            .doOnSubscribe { view?.showProgress() }
            .doFinally { view?.hideProgress() }
            .subscribeBy(
                onComplete = {
                    view?.let { start(it.clubId, it.day) }
                },
                onError = {
                    onError(it, "Ошибка при актуализации расписания")
                }
            )
            .addToComposite()
    }

    override fun onSportsActivityClicked(sportsActivity: SportsActivity) {
        logEvent(SchedulesAnalytic.Screens.DaySchedule.OnSportsActivityClicked)
        navigator.navigateTo(
            ReserveDestination(
                sportsActivity.schedule.id,
                sportsActivity.schedule.clubId
            )
        )
    }

    private fun start(clubId: Int, day: Date) {
        getDaySportsActivities(clubId, day)
            .observeOn(mainScheduler)
            .doOnSubscribe { view?.showProgress() }
            .doFinally { view?.hideProgress() }
            .subscribeBy(
                onSuccess = this::showResult,
                onError = {
                    onError(it, "Ошибка при получении расписания занятий на день")
                }
            )
            .addToComposite()
    }

    private fun showResult(sportsActivity: List<SportsActivity>) {
        view?.showSchedule(sportsActivity/*.filter { it.schedule.preEntry }*/)
    }
}
