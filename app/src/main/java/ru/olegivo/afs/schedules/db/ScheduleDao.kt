package ru.olegivo.afs.schedules.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import ru.olegivo.afs.schedules.db.models.ScheduleEntity
import java.util.*

@Dao
abstract class ScheduleDao {
    @Query("select id, clubId, [group], activity, datetime, length, room, trainer, preEntry, totalSlots from schedules where datetime >= :from and datetime < :until and clubId = :clubId")
    abstract fun getSchedules(clubId: Int, from: Date, until: Date): Maybe<List<ScheduleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun putSchedules(schedules: List<ScheduleEntity>): Completable

    @Query("select id, clubId, [group], activity, datetime, length, room, trainer, preEntry, totalSlots from schedules where id = :id")
    abstract fun getSchedule(id: Long): Single<ScheduleEntity>
}
