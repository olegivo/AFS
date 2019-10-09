package ru.olegivo.afs.schedule.presentation

import ru.olegivo.afs.schedule.android.ScheduleFragment
import ru.olegivo.afs.schedule.presentation.models.ScheduleDestination
import ru.terrakok.cicerone.android.support.SupportAppScreen

data class ScheduleScreen(val scheduleDestination: ScheduleDestination) : SupportAppScreen() {
    override fun getFragment() = ScheduleFragment()
}
