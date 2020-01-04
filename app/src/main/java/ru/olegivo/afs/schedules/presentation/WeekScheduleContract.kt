package ru.olegivo.afs.schedules.presentation

import ru.olegivo.afs.common.presentation.PresentationContract
import ru.olegivo.afs.schedules.domain.models.SportsActivity

interface WeekScheduleContract {
    interface Presenter : PresentationContract.Presenter<View> {
        fun start()
        fun onSportsActivityClicked(sportsActivity: SportsActivity)
        fun actualizeSchedule()
    }

    interface View : PresentationContract.View, PresentationContract.ErrorDisplay,
        PresentationContract.ViewWithProgress {
        fun showSchedule(sportsActivities: List<SportsActivity>)
    }
}
