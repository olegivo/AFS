package ru.olegivo.afs.favorites.db.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "recordReminderSchedules")
data class RecordReminderScheduleEntity(
    @PrimaryKey val scheduleId: Long,
    val dateFrom: Date,
    val dateUntil: Date
)
