package ru.olegivo.afs.reserve.presentation.models

import ru.olegivo.afs.common.presentation.Destination
import ru.olegivo.afs.schedule.domain.models.Schedule

data class ReserveDestination(val schedule: Schedule) : Destination
