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
        groupId = getRandomInt(),
        group = getRandomString(),
        activityId = getRandomInt(),
        activity = getRandomString(),
        // TODO: later: room = getRandomString(),
        // TODO: later: trainer = getRandomString(),
        datetime = getRandomDate(),
        length = getRandomInt(),
        preEntry = getRandomBoolean(),
        totalSlots = getRandomInt(),
        recordFrom = getRandomDate(),
        recordTo = getRandomDate()
    )
