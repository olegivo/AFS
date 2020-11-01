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

package ru.olegivo.afs.favorites.android

import android.content.Intent
import androidx.work.Data
import androidx.work.workDataOf

data class SportsActivityReserveParameters(
    val clubId: Int,
    val scheduleId: Long,
    val fio: String,
    val phone: String
)

fun Intent.putSportsActivityReserveParameters(sportsActivityReserveParameters: SportsActivityReserveParameters): Intent =
    this.putExtra("CLUB_ID", sportsActivityReserveParameters.clubId)
        .putExtra("SCHEDULE_ID", sportsActivityReserveParameters.scheduleId)
        .putExtra("FIO", sportsActivityReserveParameters.fio)
        .putExtra("PHONE", sportsActivityReserveParameters.phone)

fun Intent.getSportsActivityReserveParameters() =
    SportsActivityReserveParameters(
        clubId = getIntExtra("CLUB_ID", 0),
        scheduleId = getLongExtra("SCHEDULE_ID", 0),
        fio = getStringExtra("FIO")!!,
        phone = getStringExtra("PHONE")!!
    )

fun SportsActivityReserveParameters.toWorkerParameters() =
    workDataOf(
        "CLUB_ID" to clubId,
        "SCHEDULE_ID" to scheduleId,
        "FIO" to fio,
        "PHONE" to phone
    )

fun Data.getSportsActivityReserveParameters() =
    SportsActivityReserveParameters(
        clubId = getInt("CLUB_ID", 0),
        scheduleId = getLong("SCHEDULE_ID", 0),
        fio = getString("FIO")!!,
        phone = getString("PHONE")!!
    )
