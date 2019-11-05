package ru.olegivo.afs.schedule.presentation

import ru.olegivo.afs.schedule.android.ReserveFragment
import ru.olegivo.afs.schedule.presentation.models.ReserveDestination
import ru.terrakok.cicerone.android.support.SupportAppScreen

data class ReserveScreen(val reserveDestination: ReserveDestination) : SupportAppScreen() {
    override fun getFragment() = ReserveFragment.createInstance(reserveDestination.schedule)
}
