/*
 * Copyright (C) 2020 Oleg Ivashchenko <olegivo@gmail.com>
 *
 * This file is part of AFS.
 *
 * AFS is free software: you can redistribute it and/or modify
 * it under the terms of the MIT License.
 *
 * AFS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * AFS.
 */

package ru.olegivo.afs.favorites.domain.models

import ru.olegivo.afs.common.getMinutesOfDay
import ru.olegivo.afs.schedules.domain.models.Schedule

data class FavoriteFilter(
    val groupId: Int,
    val group: String,
    val activityId: Int,
    val activity: String,
    val dayOfWeek: Int,
    val minutesOfDay: Int
)

fun Schedule.toFavoriteFilter(): FavoriteFilter {
    return FavoriteFilter(
        groupId = groupId,
        group = group,
        activityId = activityId,
        activity = activity,
        dayOfWeek = getDayOfWeek(),
        minutesOfDay = datetime.getMinutesOfDay()
    )
}

fun Schedule.applyFilters(favoriteFilters: List<FavoriteFilter>): Boolean =
    toFavoriteFilter().let { thisScheduleFilter ->
        favoriteFilters.any { favoriteFilter ->
            favoriteFilter == thisScheduleFilter
        }
    }

fun List<Schedule>.filterByFavorites(favoriteFilters: List<FavoriteFilter>) =
    filter { schedule ->
        schedule.applyFilters(favoriteFilters)
    }
