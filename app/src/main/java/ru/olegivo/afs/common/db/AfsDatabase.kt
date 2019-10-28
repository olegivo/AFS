package ru.olegivo.afs.common.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.olegivo.afs.schedule.db.ScheduleDao
import ru.olegivo.afs.schedule.db.models.ReservedSchedule


@Database(
    entities = [
        ReservedSchedule::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AfsDatabase : RoomDatabase() {
    abstract val schedule: ScheduleDao
}
