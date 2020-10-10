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

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import ru.olegivo.afs.schedules.data.models.DataSchedule
import ru.olegivo.afs.shared.datetime.ADate
import ru.olegivo.afs.shared.network.models.Club
import ru.olegivo.afs.shared.network.models.Schedule
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME
import java.util.Date

typealias DomainClub = ru.olegivo.afs.clubs.domain.models.Club

fun Club.toDomain(): DomainClub {
    return with(this) { DomainClub(id = id, title = title) }
}

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
        datetime = datetime.toDate(),
        length = length,
        preEntry = preEntry,
        totalSlots = totalSlots,
        recordFrom = beginDate?.toDate(),
        recordTo = endDate?.toDate()
    )

fun LocalDateTime.toDate(): Date {
    return Date(toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds())
}

fun ADate.toDate(): Date {
    with(timeZone) {
        return Date(local.toInstant().toEpochMilliseconds())
    }
}

fun Instant.toDate(): Date {
    return Date(toEpochMilliseconds())
}

fun String.toDate(): Date {
    val offsetDateTime = OffsetDateTime.parse(this, ISO_OFFSET_DATE_TIME)
    val instant = offsetDateTime.toInstant()
    return Date(instant.toEpochMilli())
}
