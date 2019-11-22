package ru.olegivo.afs.schedules.network.models

import com.squareup.moshi.JsonClass
import ru.olegivo.afs.schedules.data.models.DataSchedule
import java.util.*

@JsonClass(generateAdapter = true)
data class Schedule(
    val activity: Activity,
//    val age: Any?,
    val age: Int?,
    val beginDate: String?,
    val change: Change?,
    val commercial: Boolean,
    val datetime: Date,
    val endDate: String,
    val firstFree: Boolean,
    val group: Group,
    val id: Long,
    val length: Int,
//    val level: Any?,
    val level: String?,
    val new: Boolean,
    val popular: Boolean,
    val preEntry: Boolean,
    val room: Room?,
//    val subscriptionId: Any?,
    val subscriptionId: Int?,
    val totalSlots: Int?,
    val trainers: List<Trainer>,
    val type: String
)

fun Schedule.toData(clubId: Int) =
    DataSchedule(
        id = id,
        clubId = clubId,
        group = group.title,
        activity = activity.title,
        datetime = datetime,
        length = length,
        room = room?.title,
        trainer = trainers.firstOrNull()?.title,
        preEntry = preEntry,
        totalSlots = totalSlots
    )
