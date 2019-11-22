package ru.olegivo.afs.schedules.db.models

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import ru.olegivo.afs.schedules.data.models.DataSchedule
import java.util.*

@Entity(
    tableName = "schedules",
    indices = [
        Index("datetime", "clubId")
    ]
)
data class ScheduleEntity(
    @PrimaryKey val id: Long,
    val clubId: Int,
    val group: String,
    val activity: String,
    val datetime: Date,
    val length: Int,
    val room: String?,
    val trainer: String?,
    val preEntry: Boolean,
    val totalSlots: Int?
)

fun ScheduleEntity.toData() =
    DataSchedule(
        id = id,
        clubId = clubId,
        group = group,
        activity = activity,
        datetime = datetime,
        length = length,
        room = room,
        trainer = trainer,
        preEntry = preEntry,
        totalSlots = totalSlots
    )

fun DataSchedule.toDb(): ScheduleEntity =
    ScheduleEntity(
        id = id,
        clubId = clubId,
        group = group,
        activity = activity,
        datetime = datetime,
        length = length,
        room = room,
        trainer = trainer,
        preEntry = preEntry,
        totalSlots = totalSlots
    )
