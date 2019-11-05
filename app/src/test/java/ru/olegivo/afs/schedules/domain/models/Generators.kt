package ru.olegivo.afs.schedules.domain.models

import ru.olegivo.afs.helpers.getRandomBoolean
import ru.olegivo.afs.helpers.getRandomDate
import ru.olegivo.afs.helpers.getRandomInt
import ru.olegivo.afs.helpers.getRandomLong
import ru.olegivo.afs.helpers.getRandomString
import ru.olegivo.afs.schedule.domain.models.ReserveContacts

fun createSchedule() =
    Schedule(
        getRandomLong(),
        getRandomInt(),
        getRandomString(),
        getRandomString(),
        getRandomDate(),
        getRandomInt(),
        getRandomString(),
        getRandomString(),
        getRandomBoolean(),
        getRandomInt(),
        getRandomInt(),
        getRandomBoolean()
    )

fun createReserveContacts() =
    ReserveContacts(getRandomString(), getRandomString())
