package ru.olegivo.afs.schedules.domain.models

data class SportsActivity(
    val schedule: Schedule,
    // Свободно мест: 5
    val availableSlots: Int?,
    // Я записан на это занятие
    val isReserved: Boolean
)
