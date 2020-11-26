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

package ru.olegivo.afs.settings.presentation

import ru.olegivo.afs.clubs.domain.models.Club
import ru.olegivo.afs.common.presentation.PresentationContract

interface SettingsContract {
    interface View : PresentationContract.View {
        fun showChooseClubDialog(clubs: List<Club>, selectedClub: Club?)
        fun showMessage(message: String)
        fun enableIsStubReserveCheckBox(isStubReserve: Boolean)
        fun disableIsStubReserveCheckBox()
    }

    interface Presenter : PresentationContract.Presenter<View> {
        fun onChooseClubClicked()
        fun onCurrentClubSelected(club: Club)
        fun onSetDefaultClubClicked()
        fun onDropDbBClicked()
        fun onStubReserveChecked(isChecked: Boolean)
    }
}
