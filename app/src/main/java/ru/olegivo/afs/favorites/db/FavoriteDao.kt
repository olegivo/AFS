package ru.olegivo.afs.favorites.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Single
import ru.olegivo.afs.favorites.db.modes.FavoriteFilterEntity
import ru.olegivo.afs.favorites.db.modes.RecordReminderScheduleEntity
import java.util.*

@Dao
interface FavoriteDao {
    @Insert
    fun addFilter(favoriteFilterEntity: FavoriteFilterEntity): Completable

    @Query("select id, [group], activity, dayOfWeek, timeOfDay from favoriteFilters")
    fun getFavoriteFilters(): Single<List<FavoriteFilterEntity>>

    @Query("delete from favoriteFilters where [group] = :group and activity = :activity and dayOfWeek = :dayOfWeek and timeOfDay = :timeOfDay")
    fun removeFilter(group: String, activity: String, dayOfWeek: Int, timeOfDay: Long): Completable

    @Query("select exists(select * from favoriteFilters where [group] = :group and activity = :activity and dayOfWeek = :dayOfWeek and timeOfDay = :timeOfDay)")
    fun exist(group: String, activity: String, dayOfWeek: Int, timeOfDay: Long): Single<Boolean>

    @Query("select scheduleId from recordReminderSchedules where dateFrom <= :moment and :moment <= dateUntil")
    fun getActiveRecordReminderScheduleIds(moment: Date): Single<List<Long>>

    @Insert(onConflict = OnConflictStrategy.REPLACE) // TODO: remove old reminders?
    fun addReminderToRecord(recordReminder: RecordReminderScheduleEntity): Completable
}
