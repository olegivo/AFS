package ru.olegivo.afs.schedules.db.models

import androidx.room.PrimaryKey
import ru.olegivo.afs.schedules.data.models.DataSchedule
import java.util.*

data class ScheduleDto(
    @PrimaryKey val id: Long,
    val clubId: Int,
    val groupId: Int,
    val group: String,
    val activityId: Int,
    val activity: String,
    // TODO: later: val room: String?,
    // TODO: later: val trainer: String?,
    val datetime: Date,
    val length: Int,
    val preEntry: Boolean,
    val totalSlots: Int?,
    val recordFrom: Date?,
    val recordTo: Date?
)

fun ScheduleDto.toData() =
    DataSchedule(
        id = id,
        clubId = clubId,
        groupId = groupId,
        group = group,
        activityId = activityId,
        activity = activity,
        // TODO: later: room = room,
        // TODO: later: trainer = trainer,
        datetime = datetime,
        length = length,
        preEntry = preEntry,
        totalSlots = totalSlots,
        recordFrom = recordFrom,
        recordTo = recordTo
    )
