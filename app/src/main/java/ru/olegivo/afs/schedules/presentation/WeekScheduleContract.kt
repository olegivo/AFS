package ru.olegivo.afs.schedules.presentation

import ru.olegivo.afs.common.presentation.PresentationContract
import ru.olegivo.afs.schedules.domain.models.SportsActivity
import ru.olegivo.afs.schedules.presentation.models.Day

interface WeekScheduleContract {
    interface Presenter : PresentationContract.Presenter<View> {
        fun onSportsActivityClicked(sportsActivity: SportsActivity)
        fun getClubId(): Int
        fun getDay(position: Int): Day
        fun onDayChanged(position: Int)
    }

    interface View : PresentationContract.View, PresentationContract.ErrorDisplay,
        PresentationContract.ViewWithProgress {
        fun onReady(position: Int)
    }
}
