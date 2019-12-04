package ru.olegivo.afs.schedule.presentation.models

import ru.olegivo.afs.common.presentation.Destination

data class ReserveDestination(val id: Long, val clubId: Int) : Destination
