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
