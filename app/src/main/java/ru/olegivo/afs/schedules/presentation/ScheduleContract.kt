package ru.olegivo.afs.schedules.presentation

import ru.olegivo.afs.common.presentation.PresentationContract
import ru.olegivo.afs.schedules.domain.models.Schedule

interface ScheduleContract {
    interface Presenter : PresentationContract.Presenter<View> {
        fun start()
        fun onScheduleClicked(schedule: Schedule)
    }

    interface View : PresentationContract.View, PresentationContract.ErrorDisplay,
        PresentationContract.ViewWithProgress {
        fun showSchedule(schedules: List<Schedule>)
    }
}
