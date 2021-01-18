/*
 * Copyright (C) 2021 Oleg Ivashchenko <olegivo@gmail.com>
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

import ru.olegivo.afs.common.toADate
import ru.olegivo.afs.common.toDate
import ru.olegivo.afs.schedules.data.models.DataSchedule

fun ScheduleEntity.toData() =
    DataSchedule(
        id = id,
        clubId = clubId,
        groupId = groupId,
        group = group,
        activityId = activityId,
        activity = activity,
        // TODO: later: room = room,
        // TODO: later: trainer = trainer,
        datetime = datetime.toDate(),
        length = length,
        preEntry = preEntry,
        totalSlots = totalSlots,
        recordFrom = recordFrom?.toDate(),
        recordTo = recordTo?.toDate()
    )

fun DataSchedule.toDb(): ScheduleEntity =
    ScheduleEntity(
        id = id,
        clubId = clubId,
        groupId = groupId,
        group = group,
        activityId = activityId,
        activity = activity,
        // TODO: later: room = room,
        // TODO: later: trainer = trainer,
        datetime = datetime.toADate(),
        length = length,
        preEntry = preEntry,
        totalSlots = totalSlots,
        recordFrom = recordFrom?.toADate(),
        recordTo = recordTo?.toADate()
    )
