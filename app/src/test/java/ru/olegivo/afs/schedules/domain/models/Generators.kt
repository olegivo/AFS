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
    isReserved: Boolean? = null
) = SportsActivity(
    schedule = createSchedule(datetime, totalSlots),
    availableSlots = availableSlots ?: getRandomInt(),
    isReserved = isReserved ?: getRandomBoolean()
)

fun createSchedule(
    datetime: Date? = null,
    totalSlots: Int? = null
) =
    Schedule(
        id = getRandomLong(),
        clubId = getRandomInt(),
        group = getRandomString(),
        activity = getRandomString(),
        datetime = datetime ?: getRandomDate(),
        length = getRandomInt(),
        room = getRandomString(),
        trainer = getRandomString(),
        preEntry = getRandomBoolean(),
        totalSlots = totalSlots ?: getRandomInt()
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