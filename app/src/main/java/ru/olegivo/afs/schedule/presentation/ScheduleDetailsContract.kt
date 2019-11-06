package ru.olegivo.afs.schedule.presentation

import ru.olegivo.afs.common.presentation.PresentationContract
import ru.olegivo.afs.schedule.domain.models.ReserveContacts
import ru.olegivo.afs.schedules.domain.models.SportsActivity

interface ScheduleDetailsContract {
    interface Presenter : PresentationContract.Presenter<View> {
        fun start(sportsActivity: SportsActivity)
        fun onReserveClicked(sportsActivity: SportsActivity, fio: String, phone: String)
        fun saveReserveContacts(reserveContacts: ReserveContacts)
    }

    interface View : PresentationContract.View {
        fun showScheduleToReserve(sportsActivity: SportsActivity)
        fun showSuccessReserved()
        fun showTryLater()
        fun showTheTimeHasGone()
        fun showHasNoSlotsAPriori()
        fun showHasNoSlotsAPosteriori()
        fun showNameAndPhoneShouldBeStated()
        fun setReserveContacts(reserveContacts: ReserveContacts)
        fun showAlreadyReserved()
    }
}
