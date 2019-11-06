package ru.olegivo.afs.schedules.data.models

import ru.olegivo.afs.helpers.getRandomBoolean
import ru.olegivo.afs.helpers.getRandomDate
import ru.olegivo.afs.helpers.getRandomInt
import ru.olegivo.afs.helpers.getRandomLong
import ru.olegivo.afs.helpers.getRandomString
import ru.olegivo.afs.schedules.domain.models.Slot

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
