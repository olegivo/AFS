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

package ru.olegivo.afs.schedules.domain.models

import ru.olegivo.afs.helpers.getRandomBoolean
import ru.olegivo.afs.helpers.getRandomDate
import ru.olegivo.afs.helpers.getRandomInt
import ru.olegivo.afs.helpers.getRandomLong
import ru.olegivo.afs.helpers.getRandomString
import ru.olegivo.afs.schedule.domain.models.ReserveContacts
import java.util.*

fun createSportsActivity(
    datetime: Date? = null,
    totalSlots: Int? = null,
    availableSlots: Int? = null,
    isReserved: Boolean? = null,
    isFavorite: Boolean? = null
) = SportsActivity(
    schedule = createSchedule(datetime, totalSlots),
    availableSlots = availableSlots ?: getRandomInt(),
    isReserved = isReserved ?: getRandomBoolean(),
    isFavorite = isFavorite ?: getRandomBoolean()
)

fun createSchedule(
    datetime: Date? = null,
    totalSlots: Int? = null
) =
    Schedule(
        id = getRandomLong(),
        clubId = getRandomInt(),
        groupId = getRandomInt(),
        group = getRandomString(),
        activityId = getRandomInt(),
        activity = getRandomString(),
        // TODO: later: room = getRandomString(),
        // TODO: later: trainer = getRandomString(),
        datetime = datetime ?: getRandomDate(),
        length = getRandomInt(),
        preEntry = getRandomBoolean(),
        totalSlots = totalSlots ?: getRandomInt(),
        recordFrom = getRandomDate(),
        recordTo = getRandomDate()
    )

fun createReserveContacts() =
    ReserveContacts(
        fio = getRandomString(),
        phone = getRandomString()
    )

fun createSlot(id: Long) = Slot(
    id = id,
    slots = getRandomInt()
)
