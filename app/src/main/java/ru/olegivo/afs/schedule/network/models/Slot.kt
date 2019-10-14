package ru.olegivo.afs.schedule.network.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Slot(val id: Long, val slots: Int?)