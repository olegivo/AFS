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

package ru.olegivo.afs.schedules.data

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import ru.olegivo.afs.schedules.data.models.DataSchedule
import ru.olegivo.afs.schedules.domain.models.Schedule
import java.util.*

interface ScheduleDbSource {
    fun setScheduleReserved(schedule: Schedule): Completable
    fun getReservedScheduleIds(from: Date, until: Date): Single<List<Long>>
    fun getSchedules(clubId: Int, from: Date, until: Date): Maybe<List<DataSchedule>>
    fun putSchedules(schedules: List<DataSchedule>): Completable
    fun getSchedule(id: Long): Single<DataSchedule>
    fun isScheduleReserved(scheduleId: Long): Single<Boolean>
    fun getSchedules(ids: List<Long>): Single<List<DataSchedule>>
}
