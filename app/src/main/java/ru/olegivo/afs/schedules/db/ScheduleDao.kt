package ru.olegivo.afs.schedules.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import ru.olegivo.afs.schedules.db.models.DictionaryEntry
import ru.olegivo.afs.schedules.db.models.ScheduleEntity
import java.util.*

@Dao
abstract class ScheduleDao {
    @Query("select $scheduleFields from schedules where datetime >= :from and datetime < :until and clubId = :clubId")
    abstract fun getSchedules(clubId: Int, from: Date, until: Date): Maybe<List<ScheduleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun putSchedules(schedules: List<ScheduleEntity>): Completable

    @Query("select $scheduleFields from schedules where id = :id")
    abstract fun getSchedule(id: Long): Single<ScheduleEntity>

    @Query("select $scheduleFields from schedules where id in (:ids)")
    abstract fun getSchedules(ids: List<Long>): Single<List<ScheduleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun putDictionary(list: List<DictionaryEntry>): Completable

    companion object {
        private const val scheduleFields =
            "id, clubId, groupId, [group], activityId, activity, datetime, length, preEntry, totalSlots, recordFrom, recordTo" // TODO: later: room, trainer,
    }
}
