package ru.olegivo.afs.reserve.presentation

import ru.olegivo.afs.common.presentation.PresentationContract
import ru.olegivo.afs.reserve.domain.models.ReserveContacts
import ru.olegivo.afs.schedule.domain.models.Schedule

interface ReserveContract {
    interface Presenter : PresentationContract.Presenter<View> {
        fun start(schedule: Schedule)
        fun onReserveClicked(schedule: Schedule, fio: String, phone: String)
        fun saveReserveContacts(reserveContacts: ReserveContacts)
    }

    interface View : PresentationContract.View {
        fun showScheduleToReserve(schedule: Schedule)
        fun showSuccessReserved()
        fun showTryLater()
        fun showTheTimeHasGone()
        fun showHasNoSlotsAPriori()
        fun showHasNoSlotsAPosteriori()
        fun showNameAndPhoneShouldBeStated()
        fun setReserveContacts(reserveContacts: ReserveContacts)
    }
}
