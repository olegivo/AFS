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
