package ru.olegivo.afs.schedules.network.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Slot(val id: Long, val slots: Int?)