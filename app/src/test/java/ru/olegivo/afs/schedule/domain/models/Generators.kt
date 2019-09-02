package ru.olegivo.afs.schedule.domain.models

import ru.olegivo.afs.helpers.getRandomBoolean
import ru.olegivo.afs.helpers.getRandomDate
import ru.olegivo.afs.helpers.getRandomInt
import ru.olegivo.afs.helpers.getRandomString

fun createSchedule() =
    Schedule(
        getRandomString(),
        getRandomString(),
        getRandomDate(),
        getRandomInt(),
        getRandomString(),
        getRandomString(),
        getRandomBoolean()
    )
