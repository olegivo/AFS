package ru.olegivo.afs.favorites.data.models

import ru.olegivo.afs.favorites.domain.models.FavoriteFilter
import ru.olegivo.afs.helpers.getRandomInt
import ru.olegivo.afs.helpers.getRandomLong
import ru.olegivo.afs.helpers.getRandomString

fun createFavoriteFilter(): FavoriteFilter =
    FavoriteFilter(
        group = getRandomString(),
        activity = getRandomString(),
        dayOfWeek = getRandomInt(),
        timeOfDay = getRandomLong()
    )
