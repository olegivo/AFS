package ru.olegivo.afs.favorites.db.modes

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.olegivo.afs.favorites.domain.models.FavoriteFilter

@Entity(tableName = "favoriteFilters")
data class FavoriteFilterEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val group: String,
    val activity: String,
    val dayOfWeek: Int,
    val timeOfDay: Long
)

fun FavoriteFilter.toDb() = FavoriteFilterEntity(
    group = group,
    activity = activity,
    dayOfWeek = dayOfWeek,
    timeOfDay = timeOfDay
)

fun FavoriteFilterEntity.toDomain() =
    FavoriteFilter(
        group = group,
        activity = activity,
        dayOfWeek = dayOfWeek,
        timeOfDay = timeOfDay
    )
