package ru.olegivo.afs.favorites.data.models

import ru.olegivo.afs.favorites.domain.models.FavoriteFilter
import ru.olegivo.afs.helpers.getRandomInt
import ru.olegivo.afs.helpers.getRandomLong

fun createFavoriteFilter(): FavoriteFilter =
    FavoriteFilter(
        groupId = getRandomInt(),
        activityId = getRandomInt(),
        dayOfWeek = getRandomInt(),
        timeOfDay = getRandomLong()
    )
