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
    val groupId: Int,
    val activityId: Int,
    // TODO: later: val room: String?,
    // TODO: later: val trainer: String?,
    val datetime: Date,
    val length: Int,
    val preEntry: Boolean,
    val totalSlots: Int?,
    val recordFrom: Date?,
    val recordTo: Date?
)

fun DataSchedule.toDb(): ScheduleEntity =
    ScheduleEntity(
        id = id,
        clubId = clubId,
        groupId = groupId,
        activityId = activityId,
        // TODO: later: room = room,
        // TODO: later: trainer = trainer,
        datetime = datetime,
        length = length,
        preEntry = preEntry,
        totalSlots = totalSlots,
        recordFrom = recordFrom,
        recordTo = recordTo
    )
