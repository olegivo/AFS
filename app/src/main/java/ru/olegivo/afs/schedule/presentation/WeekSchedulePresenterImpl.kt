package ru.olegivo.afs.schedule.presentation

import io.reactivex.Scheduler
import ru.olegivo.afs.clubs.domain.GetCurrentClubUseCase
import ru.olegivo.afs.common.domain.DateProvider
import ru.olegivo.afs.common.getDateWithoutTime
import ru.olegivo.afs.common.presentation.BasePresenter
import ru.olegivo.afs.common.presentation.Navigator
import ru.olegivo.afs.reserve.presentation.models.ReserveDestination
import ru.olegivo.afs.schedule.domain.GetCurrentWeekScheduleUseCase
import ru.olegivo.afs.schedule.domain.models.Schedule
import javax.inject.Inject
import javax.inject.Named

class WeekSchedulePresenter @Inject constructor(
    private val getCurrentClub: GetCurrentClubUseCase,
    private val getCurrentWeekSchedule: GetCurrentWeekScheduleUseCase,
    private val dateProvider: DateProvider,
    private val navigator: Navigator,
    @Named("main") private val mainScheduler: Scheduler
) : BasePresenter<ScheduleContract.View>(),
    ScheduleContract.Presenter {

    override fun start() {
        getCurrentClub()
            .flatMap { clubId ->
                getCurrentWeekSchedule(clubId).toMaybe()
            }
            .observeOn(mainScheduler)
            .subscribe(
                { schedules ->
                    val today = dateProvider.getDate().getDateWithoutTime()
                    view?.showSchedule(schedules.filter { it.datetime.getDateWithoutTime() == today })
                },
                {
                    view?.showErrorMessage(it.message ?: "Unknown error")
                }
            )
            .addToComposite()
    }

    override fun onScheduleClicked(schedule: Schedule) {
        navigator.navigateTo(ReserveDestination(schedule))
    }
}

