package ru.olegivo.afs.schedule.db

import androidx.room.Dao
import androidx.room.Insert
import io.reactivex.Completable
import ru.olegivo.afs.schedule.db.models.ReservedSchedule

@Dao
abstract class ScheduleDao {

    @Insert
    abstract fun addReservedSchedule(reservedSchedule: ReservedSchedule): Completable

}
