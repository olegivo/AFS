package ru.olegivo.afs.reserve.presentation

import ru.olegivo.afs.reserve.android.ReserveFragment
import ru.olegivo.afs.reserve.presentation.models.ReserveDestination
import ru.terrakok.cicerone.android.support.SupportAppScreen

data class ReserveScreen(val reserveDestination: ReserveDestination) : SupportAppScreen() {
    override fun getFragment() = ReserveFragment.createInstance(reserveDestination.schedule)
}
