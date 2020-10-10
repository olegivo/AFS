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

//@file:UseSerializers(InstantSerializer::class)
@file:UseSerializers(ADateTimeSerializer::class)

package ru.olegivo.afs.shared.network.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import ru.olegivo.afs.shared.datetime.ADate
import ru.olegivo.afs.shared.network.ADateTimeSerializer

@Serializable
data class Schedule(
    val activity: Activity,
//    val age: Any?,
    val age: Int?,
    val beginDate: ADate?,
    // TODO: later: val change: Change?,
    // TODO: later: val commercial: Boolean,
    val datetime: ADate,
    val endDate: ADate?,
    // TODO: later: val firstFree: Boolean,
    val group: Group,
    val id: Long,
    val length: Int,
//    val level: Any?,
    // TODO: later: val level: String?,
    // TODO: later: val new: Boolean,
    // TODO: later: val popular: Boolean,
    val preEntry: Boolean,
    // TODO: later: val room: Room?,
//    val subscriptionId: Any?,
    // TODO: later: val subscriptionId: Int?,
    val totalSlots: Int?//,
    // TODO: later: val trainers: List<Trainer>,
    // TODO: later: val type: String
)
