package ru.olegivo.afs.schedule.presentation

import ru.olegivo.afs.common.presentation.PresentationContract
import ru.olegivo.afs.schedule.domain.models.ReserveContacts
import ru.olegivo.afs.schedules.domain.models.SportsActivity

interface ScheduleDetailsContract {
    interface Presenter : PresentationContract.Presenter<View> {
        fun onReserveClicked(
            hasAcceptedAgreement: Boolean
        )
        fun onFavoriteClick()

        fun onAgreementClicked()
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
        fun setIsFavorite(isFavorite: Boolean)
        fun showHaveToAcceptAgreement()
        fun setAgreementAccepted()
        fun getSportsActivity(): SportsActivity
        fun getReserveContacts(): ReserveContacts?
        fun isAgreementAccepted(): Boolean
    }
}
