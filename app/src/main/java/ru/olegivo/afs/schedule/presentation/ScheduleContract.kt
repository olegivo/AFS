package ru.olegivo.afs.schedule.presentation

import ru.olegivo.afs.common.presentation.PresentationContract
import ru.olegivo.afs.schedule.domain.models.Schedule

interface ScheduleContract {
    interface Presenter : PresentationContract.Presenter<View> {
        fun start()
        fun onScheduleClicked(schedule: Schedule)
    }

    interface View : PresentationContract.View, PresentationContract.ErrorDisplay {
        fun showSchedule(schedules: List<Schedule>)
    }
}
