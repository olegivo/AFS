/*
 * Copyright (C) 2021 Oleg Ivashchenko <olegivo@gmail.com>
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

package ru.olegivo.afs.shared.schedules.db.models

import kotlinx.datetime.Instant

// TODO: get rid of it?
data class ScheduleEntity(
    val id: Long,
    val clubId: Int,
    val groupId: Int,
    val group: String,
    val activityId: Int,
    val activity: String,
    // TODO: later: val room: String?,
    // TODO: later: val trainer: String?,
    val datetime: Instant,
    val length: Int,
    val preEntry: Boolean,
    val totalSlots: Int?,
    val recordFrom: Instant?,
    val recordTo: Instant?
)
