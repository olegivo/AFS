package ru.olegivo.afs.schedules.data.models

import ru.olegivo.afs.helpers.getRandomBoolean
import ru.olegivo.afs.helpers.getRandomDate
import ru.olegivo.afs.helpers.getRandomInt
import ru.olegivo.afs.helpers.getRandomLong
import ru.olegivo.afs.helpers.getRandomString

fun createDataSchedule() =
    DataSchedule(
        getRandomLong(),
        getRandomString(),
        getRandomString(),
        getRandomDate(),
        getRandomInt(),
        getRandomString(),
        getRandomString(),
        getRandomBoolean(),
        getRandomInt()
    )

fun createSlot(id: Long) = Slot(id, getRandomInt())