package ru.olegivo.afs.common.android

import ru.olegivo.afs.common.presentation.Destination
import ru.olegivo.afs.common.presentation.Navigator
import ru.olegivo.afs.schedule.presentation.ReserveScreen
import ru.olegivo.afs.schedule.presentation.models.ReserveDestination
import ru.olegivo.afs.schedules.presentation.ScheduleScreen
import ru.olegivo.afs.schedules.presentation.models.ScheduleDestination
import ru.terrakok.cicerone.Router
import ru.terrakok.cicerone.Screen
import javax.inject.Inject

class NavigatorImpl @Inject constructor(val router: Router) : Navigator {
    override fun navigateTo(destination: Destination) {
        val screen: Screen = when (destination) {
            is ReserveDestination -> ReserveScreen(destination)
            is ScheduleDestination -> ScheduleScreen(destination)
            else -> TODO("Not implemented ($destination)")
        }
        router.navigateTo(screen)
    }

    override fun navigateBack() {
        router.exit()
    }
}