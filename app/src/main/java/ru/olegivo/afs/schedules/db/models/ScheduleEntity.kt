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
    // TODO: later: val room: String?,
    // TODO: later: val trainer: String?,
    val preEntry: Boolean,
    val totalSlots: Int?,
    val recordFrom: Date?,
    val recordTo: Date?
)

fun ScheduleEntity.toData() =
    DataSchedule(
        id = id,
        clubId = clubId,
        group = group,
        activity = activity,
        datetime = datetime,
        length = length,
        // TODO: later: room = room,
        // TODO: later: trainer = trainer,
        preEntry = preEntry,
        totalSlots = totalSlots,
        recordFrom = recordFrom,
        recordTo = recordTo
    )

fun DataSchedule.toDb(): ScheduleEntity =
    ScheduleEntity(
        id = id,
        clubId = clubId,
        group = group,
        activity = activity,
        datetime = datetime,
        length = length,
        // TODO: later: room = room,
        // TODO: later: trainer = trainer,
        preEntry = preEntry,
        totalSlots = totalSlots,
        recordFrom = recordFrom,
        recordTo = recordTo
    )
