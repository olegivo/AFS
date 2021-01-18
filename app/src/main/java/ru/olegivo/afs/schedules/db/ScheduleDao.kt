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

import io.reactivex.Maybe
import io.reactivex.Single
import ru.olegivo.afs.common.db.BaseRxDao
import ru.olegivo.afs.favorites.domain.models.FavoriteFilter
import ru.olegivo.afs.schedules.db.models.ScheduleEntity
import ru.olegivo.afs.shared.datetime.ADate

abstract class ScheduleDao : BaseRxDao<ScheduleEntity> {
    abstract fun getSchedules(clubId: Int, from: ADate, until: ADate): Maybe<List<ScheduleEntity>>

    abstract fun getSchedule(id: Long): Single<ScheduleEntity>

    abstract fun getSchedules(ids: List<Long>): Single<List<ScheduleEntity>>

    abstract fun filterSchedules(
        clubId: Int,
        groupId: Int,
        activityId: Int
    ): Single<List<ScheduleEntity>>

    fun filterSchedules(favoriteFilter: FavoriteFilter, clubId: Int) =
        with(favoriteFilter) {
            filterSchedules(
                clubId = clubId,
                groupId = groupId,
                activityId = activityId
            )
        }
}
