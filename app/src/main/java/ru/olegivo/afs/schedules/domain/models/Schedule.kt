package ru.olegivo.afs.schedules.domain.models

import ru.olegivo.afs.common.get
import ru.olegivo.afs.common.getDateWithoutTime
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
    val totalSlots: Int?
) {
    fun getTimeOfDay() = datetime.let {
        it.time - it.getDateWithoutTime().time
    }

    fun getDayOfWeek() = datetime.get(Calendar.DAY_OF_WEEK)
}