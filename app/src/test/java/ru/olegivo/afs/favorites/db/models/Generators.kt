package ru.olegivo.afs.favorites.db.models

import ru.olegivo.afs.favorites.db.modes.FavoriteFilterEntity
import ru.olegivo.afs.helpers.getRandomInt
import ru.olegivo.afs.helpers.getRandomLong
import ru.olegivo.afs.helpers.getRandomString

fun createFavoriteFilterEntity() =
    FavoriteFilterEntity(
        group = getRandomString(),
        activity = getRandomString(),
        dayOfWeek = getRandomInt(),
        timeOfDay = getRandomLong()
    )