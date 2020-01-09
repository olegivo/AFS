package ru.olegivo.afs.schedules.network.models

import com.squareup.moshi.JsonClass
import ru.olegivo.afs.schedules.data.models.DataSchedule
import java.util.*

@JsonClass(generateAdapter = true)
data class Schedule(
    val activity: Activity,
//    val age: Any?,
    val age: Int?,
    val beginDate: Date?,
    // TODO: later: val change: Change?,
    // TODO: later: val commercial: Boolean,
    val datetime: Date,
    val endDate: Date?,
    // TODO: later: val firstFree: Boolean,
    val group: Group,
    val id: Long,
    val length: Int,
//    val level: Any?,
    // TODO: later: val level: String?,
    // TODO: later: val new: Boolean,
    // TODO: later: val popular: Boolean,
    val preEntry: Boolean,
    // TODO: later: val room: Room?,
//    val subscriptionId: Any?,
    // TODO: later: val subscriptionId: Int?,
    val totalSlots: Int?//,
    // TODO: later: val trainers: List<Trainer>,
    // TODO: later: val type: String
)

fun Schedule.toData(clubId: Int) =
    DataSchedule(
        id = id,
        clubId = clubId,
        groupId = group.id,
        group = group.title,
        activityId = activity.id,
        activity = activity.title,
        // TODO: later: room = room?.title,
        // TODO: later: trainer = trainers.firstOrNull()?.title,
        datetime = datetime,
        length = length,
        preEntry = preEntry,
        totalSlots = totalSlots,
        recordFrom = beginDate,
        recordTo = endDate
    )
