package ru.olegivo.afs.schedules.data.models

import java.util.*

data class DataSchedule(
    val id: Long,
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
    val totalSlots: Int?
)