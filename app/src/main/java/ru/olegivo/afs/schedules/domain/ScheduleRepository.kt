/*
 * Copyright (C) 2021 Oleg Ivashchenko <olegivo@gmail.com>
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

package ru.olegivo.afs.schedules.domain

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import ru.olegivo.afs.favorites.domain.models.FavoriteFilter
import ru.olegivo.afs.schedules.domain.models.Schedule
import ru.olegivo.afs.schedules.domain.models.Slot
import java.util.Date

interface ScheduleRepository {
    fun getCurrentWeekSchedule(clubId: Int): Maybe<List<Schedule>>
    fun getDaySchedule(clubId: Int, day: Date): Maybe<List<Schedule>>
    fun getSlots(clubId: Int, ids: List<Long>): Single<List<Slot>>
    fun setScheduleReserved(schedule: Schedule): Completable
    fun getCurrentWeekReservedScheduleIds(): Single<List<Long>>
    fun getDayReservedScheduleIds(day: Date): Single<List<Long>>
    fun actualizeSchedules(clubId: Int): Single<List<Schedule>>
    fun getSchedule(scheduleId: Long): Single<Schedule>
    fun isScheduleReserved(scheduleId: Long): Single<Boolean>
    fun getSchedules(ids: List<Long>): Single<List<Schedule>>
    fun filterSchedules(favoriteFilter: FavoriteFilter, clubId: Int): Single<List<Schedule>>
}
