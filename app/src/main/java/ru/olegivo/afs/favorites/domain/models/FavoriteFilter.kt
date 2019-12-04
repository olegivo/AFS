package ru.olegivo.afs.favorites.domain.models

import ru.olegivo.afs.schedules.domain.models.Schedule

data class FavoriteFilter(
    val group: String,
    val activity: String,
    val dayOfWeek: Int,
    val timeOfDay: Long
)

fun Schedule.toFavoriteFilter(): FavoriteFilter {
    return FavoriteFilter(
        group = group,
        activity = activity,
        dayOfWeek = getDayOfWeek(),
        timeOfDay = getTimeOfDay()
    )
}

fun Schedule.applyFilters(favoriteFilters: List<FavoriteFilter>): Boolean =
    favoriteFilters.any { favoriteFilter ->
        favoriteFilter == toFavoriteFilter()
    }

fun List<Schedule>.filterByFavorites(favoriteFilters: List<FavoriteFilter>) =
    filter { schedule ->
        schedule.applyFilters(favoriteFilters)
    }