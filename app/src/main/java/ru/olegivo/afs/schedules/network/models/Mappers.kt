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

import ru.olegivo.afs.schedules.data.models.DataSchedule
import ru.olegivo.afs.shared.network.models.Club
import ru.olegivo.afs.shared.network.models.Schedule

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
        datetime = datetime,
        length = length,
        preEntry = preEntry,
        totalSlots = totalSlots,
        recordFrom = beginDate,
        recordTo = endDate
    )
