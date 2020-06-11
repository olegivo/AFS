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
import ru.olegivo.afs.schedules.db.models.DictionaryEntry
import ru.olegivo.afs.schedules.db.models.DictionaryKind
import ru.olegivo.afs.schedules.db.models.ScheduleDto
import ru.olegivo.afs.schedules.db.models.ScheduleEntity
import java.util.*

@Dao
abstract class ScheduleDao {
    @Query("select $scheduleFields from schedules $scheduleJoins where datetime >= :from and datetime < :until and clubId = :clubId")
    abstract fun getSchedules(clubId: Int, from: Date, until: Date): Maybe<List<ScheduleDto>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun putSchedules(schedules: List<ScheduleEntity>): Completable

    @Query("select $scheduleFields from schedules $scheduleJoins where id = :id")
    abstract fun getSchedule(id: Long): Single<ScheduleDto>

    @Query("select $scheduleFields from schedules $scheduleJoins where id in (:ids)")
    abstract fun getSchedules(ids: List<Long>): Single<List<ScheduleDto>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun putDictionary(list: List<DictionaryEntry>): Completable

    companion object {
        private const val DICTIONARY_GROUP = DictionaryKind.GROUP_ID
        private const val DICTIONARY_ACTIVITY = DictionaryKind.ACTIVITY_ID

        private const val scheduleFields =
            "id, clubId, groupId, groups.value as [group], activityId, activities.value as activity, datetime, length, preEntry, totalSlots, recordFrom, recordTo" // TODO: later: room, trainer,

        private const val scheduleJoins = " as s " +
                "inner join dictionary as groups on s.groupId = groups.key and groups.dictionaryId = $DICTIONARY_GROUP " +
                "inner join dictionary as activities on s.activityId = activities.key and activities.dictionaryId = $DICTIONARY_ACTIVITY "
    }
}
