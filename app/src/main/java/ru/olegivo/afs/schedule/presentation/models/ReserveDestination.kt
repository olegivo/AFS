package ru.olegivo.afs.schedule.presentation.models

import ru.olegivo.afs.common.presentation.Destination
import ru.olegivo.afs.schedules.domain.models.Schedule

data class ReserveDestination(val schedule: Schedule) : Destination
