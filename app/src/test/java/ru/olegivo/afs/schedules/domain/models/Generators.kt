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
