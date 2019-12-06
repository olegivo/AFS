package ru.olegivo.afs.schedules.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Single
import ru.olegivo.afs.schedules.db.models.ReservedSchedule
import java.util.*

@Dao
abstract class ReserveDao {

    @Insert
    abstract fun addReservedSchedule(reservedSchedule: ReservedSchedule): Completable

    @Query("select id from reservedSchedules where datetime >= :from and datetime < :until")
    abstract fun getReservedScheduleIds(from: Date, until: Date): Single<List<Long>>

    @Query("select exists (select * from reservedSchedules where id = :scheduleId)")
    abstract fun isScheduleReserved(scheduleId: Long): Single<Boolean>
}
