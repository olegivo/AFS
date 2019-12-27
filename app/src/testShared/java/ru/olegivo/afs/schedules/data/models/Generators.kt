package ru.olegivo.afs.schedules.data.models

import ru.olegivo.afs.helpers.getRandomBoolean
import ru.olegivo.afs.helpers.getRandomDate
import ru.olegivo.afs.helpers.getRandomInt
import ru.olegivo.afs.helpers.getRandomLong
import ru.olegivo.afs.helpers.getRandomString

fun createDataSchedule() =
    DataSchedule(
        id = getRandomLong(),
        clubId = getRandomInt(),
        group = getRandomString(),
        activity = getRandomString(),
        datetime = getRandomDate(),
        length = getRandomInt(),
        room = getRandomString(),
        trainer = getRandomString(),
        preEntry = getRandomBoolean(),
        totalSlots = getRandomInt(),
        recordFrom = getRandomDate(),
        recordTo = getRandomDate()
    )
