/*
 * Copyright (C) 2020 Oleg Ivashchenko <olegivo@gmail.com>
 *
 * This file is part of AFS.
 *
 * AFS is free software: you can redistribute it and/or modify
 * it under the terms of the MIT License.
 *
 * AFS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * AFS.
 */

package ru.olegivo.afs.schedule.presentation

import ru.olegivo.afs.common.presentation.PresentationContract
import ru.olegivo.afs.schedule.domain.models.ReserveContacts
import ru.olegivo.afs.schedules.presentation.models.SportsActivityDisplay

interface ScheduleDetailsContract {
    interface Presenter : PresentationContract.Presenter<View> {
        fun init(scheduleId: Long, clubId: Int)
        fun onReserveClicked(hasAcceptedAgreement: Boolean)
        fun onFavoriteClick()
        fun onAgreementClicked()
    }

    interface View : PresentationContract.View {
        fun showScheduleToReserve(sportsActivity: SportsActivityDisplay)
        fun showSuccessReserved()
        fun showTryLater()
        fun showTheTimeHasGone()
        fun showHasNoSlotsAPriori()
        fun showHasNoSlotsAPosteriori()
        fun showNameAndPhoneShouldBeStated()
        fun setReserveContacts(reserveContacts: ReserveContacts)
        fun showAlreadyReserved()
        fun showIsFavorite(isFavorite: Boolean)
        fun showHaveToAcceptAgreement()
        fun setAgreementAccepted()
        fun getReserveContacts(): ReserveContacts?
        fun isAgreementAccepted(): Boolean
    }
}
