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
        fio = getStringExtra("FIO"),
        phone = getStringExtra("PHONE")
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