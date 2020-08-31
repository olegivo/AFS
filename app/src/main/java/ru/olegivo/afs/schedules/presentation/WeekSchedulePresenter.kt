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
import org.jetbrains.annotations.TestOnly
import ru.olegivo.afs.clubs.domain.GetCurrentClubUseCase
import ru.olegivo.afs.common.add
import ru.olegivo.afs.common.domain.DateProvider
import ru.olegivo.afs.common.domain.ErrorReporter
import ru.olegivo.afs.common.firstDayOfWeek
import ru.olegivo.afs.common.presentation.BasePresenter
import ru.olegivo.afs.common.presentation.Navigator
import ru.olegivo.afs.schedule.presentation.models.ReserveDestination
import ru.olegivo.afs.schedules.domain.models.SportsActivity
import ru.olegivo.afs.schedules.presentation.models.Day
import javax.inject.Inject
import javax.inject.Named

class WeekSchedulePresenter @Inject constructor(
    private val getCurrentClub: GetCurrentClubUseCase,
    private val dateProvider: DateProvider,
    private val navigator: Navigator,
    @Named("main") private val mainScheduler: Scheduler,
    errorReporter: ErrorReporter
) : BasePresenter<WeekScheduleContract.View>(errorReporter),
    WeekScheduleContract.Presenter {

    private var clubId = 0
    private var currentDay: Int = -1
    private lateinit var days: List<Day>

    override fun bindView(view: WeekScheduleContract.View) {
        super.bindView(view)
        if (clubId == 0) {
            start()
        } else {
            showResult()
        }
    }

    override fun getClubId(): Int = clubId

    override fun getDay(position: Int): Day = days[position]

    override fun onDayChanged(position: Int) {
        currentDay = position
    }

    override fun onSportsActivityClicked(sportsActivity: SportsActivity) {
        navigator.navigateTo(
            ReserveDestination(
                sportsActivity.schedule.id,
                sportsActivity.schedule.clubId
            )
        )
    }

    @TestOnly
    internal fun clear() {
        clubId = 0
        currentDay = -1
    }

    private fun start() {
        val now = dateProvider.getDate()
        currentDay = dateProvider.getCurrentWeekDayNumber() - 1
        days = firstDayOfWeek(now)
            .let { firstDayOfWeek ->
                arrayOf("ПН", "ВТ", "СР", "ЧТ", "ПТ", "СБ", "ВС")
                    .mapIndexed { index, dayName ->
                        val weekDay = firstDayOfWeek.add(days = index)
                        Day(
                            caption = dayName,
                            date = weekDay
                        )
                    }
            }

        getCurrentClub()
            .doOnSuccess { clubId = it }
            .map { Unit }
            .observeOn(mainScheduler)
            .doOnSubscribe { view?.showProgress() }
            .doFinally { view?.hideProgress() }
            .subscribeBy(
                onSuccess = { showResult() },
                onError = {
                    onError(it, "Ошибка при получении текущего клуба")
                }
            )
            .addToComposite()
    }

    private fun showResult() {
        view?.onReady(currentDay)
    }
}
