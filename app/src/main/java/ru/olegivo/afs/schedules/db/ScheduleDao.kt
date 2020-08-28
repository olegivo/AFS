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

package ru.olegivo.afs.schedules.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import ru.olegivo.afs.schedules.db.models.ScheduleEntity
import java.util.Date

@Dao
abstract class ScheduleDao {
    @Query(
        """select $scheduleFields 
                from schedules 
                where datetime >= :from and datetime < :until and clubId = :clubId"""
    )
    abstract fun getSchedules(clubId: Int, from: Date, until: Date): Maybe<List<ScheduleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun putSchedules(schedules: List<ScheduleEntity>): Completable

    @Query("select $scheduleFields from schedules where id = :id")
    abstract fun getSchedule(id: Long): Single<ScheduleEntity>

    @Query("select $scheduleFields from schedules where id in (:ids)")
    abstract fun getSchedules(ids: List<Long>): Single<List<ScheduleEntity>>

    companion object {
        private const val scheduleFields =
            """id, clubId, groupId, [group], activityId, activity, datetime, length, 
preEntry, totalSlots, recordFrom, recordTo""" // TODO: later: room, trainer,
    }
}
