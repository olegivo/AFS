package ru.olegivo.afs.reserve.domain.models


data class Reserve(
    val fio: String,
    val phone: String,
    val scheduleId: Long,
    val clubId: Int
)
