/*
 * Copyright (C) 2020 Oleg Ivashchenko <olegivo@gmail.com>
 *
 * This file is part of AFS.
 *
 * AFS is free software: you can redistribute it and/or modify
 * it under the terms of the MIT License.
 *
 * AFS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * AFS.
 */

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
