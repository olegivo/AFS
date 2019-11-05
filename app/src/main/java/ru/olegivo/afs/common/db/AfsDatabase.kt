package ru.olegivo.afs.common.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.olegivo.afs.favorites.db.FavoriteDao
import ru.olegivo.afs.favorites.db.modes.FavoriteFilterEntity
import ru.olegivo.afs.schedules.db.ScheduleDao
import ru.olegivo.afs.schedules.db.models.ReservedSchedule


@Database(
    entities = [
        ReservedSchedule::class,
        FavoriteFilterEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AfsDatabase : RoomDatabase() {
    abstract val favorites: FavoriteDao
    abstract val schedule: ScheduleDao
}
