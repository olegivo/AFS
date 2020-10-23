/*
 * Copyright (C) 2020 Oleg Ivashchenko <olegivo@gmail.com>
 *
 * This file is part of AFS.
 *
 * AFS is free software: you can redistribute it and/or modify
 * it under the terms of the MIT License.
 *
 * AFS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * AFS.
 */

package ru.olegivo.afs.favorites.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Single
import ru.olegivo.afs.favorites.db.models.FavoriteFilterEntity
import ru.olegivo.afs.favorites.db.models.RecordReminderScheduleEntity
import java.util.*

@Dao
interface FavoriteDao {
    @Insert
    fun addFilter(favoriteFilterEntity: FavoriteFilterEntity): Completable

    @Query("select id, groupId, `group`, activityId, activity, dayOfWeek, minutesOfDay from favoriteFilters")
    fun getFavoriteFilters(): Single<List<FavoriteFilterEntity>>

    @Query(
        """delete from favoriteFilters 
where groupId = :groupId and activityId = :activityId and dayOfWeek = :dayOfWeek and minutesOfDay = :minutesOfDay"""
    )
    fun removeFilter(groupId: Int, activityId: Int, dayOfWeek: Int, minutesOfDay: Int): Completable

    @Query(
        """select exists
(select * from favoriteFilters 
where groupId = :groupId and activityId = :activityId and dayOfWeek = :dayOfWeek and minutesOfDay = :minutesOfDay)"""
    )
    fun exist(groupId: Int, activityId: Int, dayOfWeek: Int, minutesOfDay: Int): Single<Boolean>

    @Query("select scheduleId from recordReminderSchedules where dateFrom <= :moment and :moment <= dateUntil")
    fun getActiveRecordReminderScheduleIds(moment: Date): Single<List<Long>>

    @Insert(onConflict = OnConflictStrategy.REPLACE) // TODO: remove old reminders?
    fun addReminderToRecord(recordReminder: RecordReminderScheduleEntity): Completable

    @Query("select exists(select * from recordReminderSchedules where scheduleId = :scheduleId)")
    fun hasPlannedReminderToRecord(scheduleId: Long): Single<Boolean>
}
