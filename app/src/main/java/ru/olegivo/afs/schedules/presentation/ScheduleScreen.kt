package ru.olegivo.afs.schedules.presentation

import ru.olegivo.afs.schedules.android.ScheduleFragment
import ru.olegivo.afs.schedules.presentation.models.ScheduleDestination
import ru.terrakok.cicerone.android.support.SupportAppScreen

data class ScheduleScreen(val scheduleDestination: ScheduleDestination) : SupportAppScreen() {
    override fun getFragment() = ScheduleFragment()
}
