package ru.olegivo.afs.schedule.presentation.models

import ru.olegivo.afs.common.presentation.Destination
import ru.olegivo.afs.schedules.domain.models.SportsActivity

data class ReserveDestination(val sportsActivity: SportsActivity) : Destination
