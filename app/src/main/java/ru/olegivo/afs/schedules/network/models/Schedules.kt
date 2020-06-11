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

package ru.olegivo.afs.schedules.network.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Schedules(
    // TODO: later val club: Club,
    val dateSince: String,
    val dateTo: String,
    val entryEnabled: Boolean,
    val isNew: Boolean,
    val needSlots: Boolean,
    val next: String?,
    val prev: String?,
    val schedule: List<Schedule>
)

@JsonClass(generateAdapter = true)
data class Club(
    val barCodeType: String,
//    val externalBaseId: Any?,
//    val externalId: Any?,
    val externalBaseId: Int?,
    val externalId: Int?,
    val id: Int,
    val timezone: String,
    val title: String
)

@JsonClass(generateAdapter = true)
data class Room(
    val id: Int,
    val sortOrder: Int,
    val title: String
)

@JsonClass(generateAdapter = true)
data class Trainer(
    val clubs: List<Int>,
    val facePhoto: String?,
    val facebookLink: String,
    val id: String,
    val instagramLink: String,
    val phone: String?,
    val photo: String?,
    val position: String,
    val sortOrder: Int,
    val title: String,
    val url: String,
    val vkLink: String
)

@JsonClass(generateAdapter = true)
data class Change(
    val activity: ActivityX?,
    val age: Int?,
    val datetime: String?,
    val group: Group?,
    val id: String,
    val length: Int?,
//    val level: Any?,
    val level: Int?,
    val note: String?,
    val publishDatetime: String,
    val room: Room?,
    val silent: Boolean,
    val title: String,
    val trainers: List<Trainer>?,
    val type: String
)

@JsonClass(generateAdapter = true)
data class Activity(
    val color: String?,
    val description: String?,
    val id: Int,
    val length: Int?,
    val title: String,
    val type: String,
    val typeId: Int,
    val url: String?,
    val youtubePreviewUrl: String?,
    val youtubeUrl: String?
)

@JsonClass(generateAdapter = true)
data class Group(
    val id: Int,
    val sortOrder: Int,
    val title: String
)

@JsonClass(generateAdapter = true)
data class ActivityX(
    val color: String?,
    val id: Int,
    val title: String,
    val type: String,
    val typeId: Int,
    val url: String?,
    val youtubePreviewUrl: String?,
    val youtubeUrl: String?
)
