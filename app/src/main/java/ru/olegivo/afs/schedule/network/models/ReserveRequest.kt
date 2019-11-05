package ru.olegivo.afs.schedule.network.models

import com.squareup.moshi.JsonClass
import ru.olegivo.afs.schedule.domain.models.Reserve


@JsonClass(generateAdapter = true)
data class ReserveRequest(
    val fio: String,
    val phone: String,
    val scheduleId: Long,
    val clubId: Int
)

fun Reserve.toNetwork() = ReserveRequest(fio, phone, scheduleId, clubId)