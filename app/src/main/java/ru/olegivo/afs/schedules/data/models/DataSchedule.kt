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

package ru.olegivo.afs.schedules.data.models

import ru.olegivo.afs.schedules.domain.models.Schedule
import java.util.*

data class DataSchedule(
    val id: Long,
    val clubId: Int,
    // Направление - Игровые виды спорта
    val groupId: Int,
    // Занятие - Волейбол клиенты
    val group: String,
    // 08:30 - 10:00
    val activityId: Int,
    val activity: String,
    // Игровой зал.
    // TODO: later: val room: String?,
    // Инструкторы - Цхададзе Алекси
    // TODO: later: val trainer: String?,
    // Предварительная запись
    val datetime: Date,
    // Всего мест: 21
    val length: Int,
    val preEntry: Boolean,
    val totalSlots: Int?,
    val recordFrom: Date?,
    val recordTo: Date?
)

fun DataSchedule.toDomain(): Schedule {
    return Schedule(
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
}
