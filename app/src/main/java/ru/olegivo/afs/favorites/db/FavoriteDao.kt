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

import io.reactivex.Completable
import io.reactivex.Single
import ru.olegivo.afs.common.db.BaseRxDao
import ru.olegivo.afs.favorites.db.models.FavoriteFilterEntity
import ru.olegivo.afs.favorites.db.models.RecordReminderScheduleEntity
import java.util.Date

interface FavoriteDao : BaseRxDao<FavoriteFilterEntity> {

    fun getFavoriteFilters(): Single<List<FavoriteFilterEntity>>

    fun removeFilter(groupId: Int, activityId: Int, dayOfWeek: Int, minutesOfDay: Int): Completable

    fun exist(groupId: Int, activityId: Int, dayOfWeek: Int, minutesOfDay: Int): Single<Boolean>

    fun getActiveRecordReminderScheduleIds(moment: Date): Single<List<Long>>

    fun addReminderToRecord(recordReminder: RecordReminderScheduleEntity): Completable

    fun hasPlannedReminderToRecord(scheduleId: Long): Single<Boolean>
}
