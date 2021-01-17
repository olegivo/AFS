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

import ru.olegivo.afs.common.db.BaseDao
import ru.olegivo.afs.shared.datetime.ADate
import ru.olegivo.afs.shared.favorites.db.models.FavoriteFilterEntity
import ru.olegivo.afs.shared.favorites.db.models.RecordReminderScheduleEntity

interface FavoriteDao : BaseDao<FavoriteFilterEntity> {

    suspend fun getFavoriteFilters(): List<FavoriteFilterEntity>
    fun removeFilter(groupId: Int, activityId: Int, dayOfWeek: Int, minutesOfDay: Int)
    suspend fun exist(groupId: Int, activityId: Int, dayOfWeek: Int, minutesOfDay: Int): Boolean
    suspend fun getActiveRecordReminderScheduleIds(moment: ADate): List<Long>
    fun addReminderToRecord(recordReminder: RecordReminderScheduleEntity)
    suspend fun hasPlannedReminderToRecord(scheduleId: Long): Boolean
}
