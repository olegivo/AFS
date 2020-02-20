package ru.olegivo.afs.favorites.db.modes

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.olegivo.afs.favorites.domain.models.FavoriteFilter

@Entity(tableName = "favoriteFilters")
data class FavoriteFilterEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val groupId: Int,
    val activityId: Int,
    val dayOfWeek: Int,
    val timeOfDay: Long
)

fun FavoriteFilter.toDb() = FavoriteFilterEntity(
    groupId = groupId,
    activityId = activityId,
    dayOfWeek = dayOfWeek,
    timeOfDay = timeOfDay
)

fun FavoriteFilterEntity.toDomain() =
    FavoriteFilter(
        groupId = groupId,
        activityId = activityId,
        dayOfWeek = dayOfWeek,
        timeOfDay = timeOfDay
    )
