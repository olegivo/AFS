package ru.olegivo.afs.schedules.presentation

import ru.olegivo.afs.common.presentation.PresentationContract
import ru.olegivo.afs.schedules.domain.models.SportsActivity
import java.util.*

interface DayScheduleContract {
    interface Presenter : PresentationContract.Presenter<View> {
        fun onSportsActivityClicked(sportsActivity: SportsActivity)
        fun actualizeSchedule()
    }

    interface View : PresentationContract.View, PresentationContract.ErrorDisplay,
        PresentationContract.ViewWithProgress {
        val day: Date
        val clubId: Int

        fun showSchedule(sportsActivities: List<SportsActivity>)
    }
}
