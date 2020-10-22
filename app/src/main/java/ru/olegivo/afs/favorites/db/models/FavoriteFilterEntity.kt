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

package ru.olegivo.afs.favorites.db.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.olegivo.afs.favorites.domain.models.FavoriteFilter

@Entity(tableName = "favoriteFilters")
data class FavoriteFilterEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val groupId: Int,
    val group: String,
    val activityId: Int,
    val activity: String,
    val dayOfWeek: Int,
    val timeOfDay: Long
)

fun FavoriteFilter.toDb() = FavoriteFilterEntity(
    groupId = groupId,
    group = group,
    activityId = activityId,
    activity = activity,
    dayOfWeek = dayOfWeek,
    timeOfDay = timeOfDay
)

fun FavoriteFilterEntity.toDomain() =
    FavoriteFilter(
        groupId = groupId,
        group = group,
        activityId = activityId,
        activity = activity,
        dayOfWeek = dayOfWeek,
        timeOfDay = timeOfDay
    )
