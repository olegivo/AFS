package ru.olegivo.afs.favorites.db.models

import ru.olegivo.afs.favorites.db.modes.FavoriteFilterEntity
import ru.olegivo.afs.helpers.getRandomInt
import ru.olegivo.afs.helpers.getRandomLong

fun createFavoriteFilterEntity() =
    FavoriteFilterEntity(
        groupId = getRandomInt(),
        activityId = getRandomInt(),
        dayOfWeek = getRandomInt(),
        timeOfDay = getRandomLong()
    )
