package ru.olegivo.afs.schedules.presentation

import io.reactivex.Maybe
import io.reactivex.Scheduler
import io.reactivex.rxkotlin.subscribeBy
import ru.olegivo.afs.clubs.domain.GetCurrentClubUseCase
import ru.olegivo.afs.common.domain.DateProvider
import ru.olegivo.afs.common.getDateWithoutTime
import ru.olegivo.afs.common.presentation.BasePresenter
import ru.olegivo.afs.common.presentation.Navigator
import ru.olegivo.afs.schedule.presentation.models.ReserveDestination
import ru.olegivo.afs.schedules.domain.ActualizeScheduleUseCase
import ru.olegivo.afs.schedules.domain.GetCurrentWeekScheduleUseCase
import ru.olegivo.afs.schedules.domain.models.SportsActivity
import javax.inject.Inject
import javax.inject.Named

class WeekSchedulePresenter @Inject constructor(
    private val getCurrentClub: GetCurrentClubUseCase,
    private val getCurrentWeekSchedule: GetCurrentWeekScheduleUseCase,
    private val actualizeSchedule: ActualizeScheduleUseCase,
    private val dateProvider: DateProvider,
    private val navigator: Navigator,
    @Named("main") private val mainScheduler: Scheduler
) : BasePresenter<WeekScheduleContract.View>(),
    WeekScheduleContract.Presenter {

    override fun bindView(view: WeekScheduleContract.View) {
        super.bindView(view)
        start()
    }

    override fun actualizeSchedule() {
        getCurrentClub()
            .flatMap { clubId ->
                actualizeSchedule.invoke(clubId)
                    .andThen(Maybe.defer {
                        getCurrentWeekSchedule(clubId)
                    })
            }
            .observeOn(mainScheduler)
            .doOnSubscribe { view?.showProgress() }
            .doFinally { view?.hideProgress() }
            .subscribeBy(
                onSuccess = this::showResult,
                onError = this::showError
            )
            .addToComposite()
    }

    override fun onSportsActivityClicked(sportsActivity: SportsActivity) {
        navigator.navigateTo(
            ReserveDestination(
                sportsActivity.schedule.id,
                sportsActivity.schedule.clubId
            )
        )
    }

    private fun start() {
        getCurrentClub()
            .flatMap { clubId ->
                getCurrentWeekSchedule(clubId)
            }
            .observeOn(mainScheduler)
            .doOnSubscribe { view?.showProgress() }
            .doFinally { view?.hideProgress() }
            .subscribeBy(
                onSuccess = this::showResult,
                onError = this::showError
            )
            .addToComposite()
    }

    private fun showError(it: Throwable) {
        onError(it, it.message ?: "Unknown error")
    }

    private fun showResult(sportsActivity: List<SportsActivity>) {
        val today = dateProvider.getDate().getDateWithoutTime()
        view?.showSchedule(sportsActivity.filter { it.schedule.preEntry && it.schedule.datetime.getDateWithoutTime() == today })
    }
}

