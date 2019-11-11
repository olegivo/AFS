package ru.olegivo.afs.schedules.db.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "reservedSchedules")
data class ReservedSchedule(
    @PrimaryKey val id: Long,
    val datetime: Date
)