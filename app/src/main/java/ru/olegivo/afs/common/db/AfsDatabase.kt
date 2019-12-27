package ru.olegivo.afs.common.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.olegivo.afs.favorites.db.FavoriteDao
import ru.olegivo.afs.favorites.db.modes.FavoriteFilterEntity
import ru.olegivo.afs.favorites.db.modes.RecordReminderScheduleEntity
import ru.olegivo.afs.schedules.db.ReserveDao
import ru.olegivo.afs.schedules.db.ScheduleDao
import ru.olegivo.afs.schedules.db.models.ReservedSchedule
import ru.olegivo.afs.schedules.db.models.ScheduleEntity


@Database(
    entities = [
        ScheduleEntity::class,
        ReservedSchedule::class,
        FavoriteFilterEntity::class,
        RecordReminderScheduleEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AfsDatabase : RoomDatabase() {
    abstract val schedules: ScheduleDao
    abstract val favorites: FavoriteDao
    abstract val reserve: ReserveDao
}
