package ru.olegivo.afs.schedule.presentation

import io.reactivex.Scheduler
import ru.olegivo.afs.common.getDateWithoutTime
import ru.olegivo.afs.common.presentation.BasePresenter
import ru.olegivo.afs.common.domain.DateProvider
import ru.olegivo.afs.schedule.domain.GetCurrentWeekScheduleUseCase
import javax.inject.Inject
import javax.inject.Named

class WeekSchedulePresenter @Inject constructor(
    private val getCurrentWeekSchedule: GetCurrentWeekScheduleUseCase,
    private val dateProvider: DateProvider,
    @Named("main") private val mainScheduler: Scheduler
) : BasePresenter<ScheduleContract.View>(),
    ScheduleContract.Presenter {

    override fun start(clubId: Int) {
        getCurrentWeekSchedule(clubId)
            .observeOn(mainScheduler)
            .subscribe(
                { schedules ->
                    val today = dateProvider.getDate().getDateWithoutTime()
                    view?.showSchedule(schedules.filter { it.datetime.getDateWithoutTime() == today })
                },
                {}
            )
            .addToComposite()
    }
}

