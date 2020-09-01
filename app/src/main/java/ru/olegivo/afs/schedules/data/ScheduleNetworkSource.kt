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

import ru.olegivo.afs.schedules.data.models.DataSchedule
import ru.olegivo.afs.schedules.domain.models.Slot
import ru.olegivo.afs.shared.network.models.Schedules

interface ScheduleNetworkSource {

    suspend fun getSchedules(clubId: Int): Schedules
    suspend fun getSchedule(clubId: Int): List<DataSchedule>
    suspend fun getSlots(clubId: Int, ids: List<Long>): List<Slot>
    suspend fun getNextSchedule(schedules: Schedules): Schedules?
    suspend fun getPrevSchedule(schedules: Schedules): Schedules?
}
