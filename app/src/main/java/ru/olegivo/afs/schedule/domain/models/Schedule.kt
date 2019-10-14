package ru.olegivo.afs.schedule.domain.models

import java.util.*

data class Schedule(
    val id: Long,
    val clubId: Int,
    // Направление - Игровые виды спорта
    val group: String,
    // Занятие - Волейбол клиенты
    val activity: String,
    // 08:30 - 10:00
    val datetime: Date,
    val length: Int,
    // Игровой зал.
    val room: String?,
    // Инструкторы - Цхададзе Алекси
    val trainer: String?,
    // Предварительная запись
    val preEntry: Boolean,
    // Всего мест: 21
    val totalSlots: Int?,
    // Свободно мест: 5
    val availableSlots: Int?
)