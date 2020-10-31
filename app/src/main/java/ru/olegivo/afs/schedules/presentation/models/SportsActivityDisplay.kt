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

package ru.olegivo.afs.schedules.presentation.models

import ru.olegivo.afs.schedules.domain.models.SportsActivity

data class SportsActivityDisplay(
    val hasAvailableSlots: Boolean,
    val preEntry: Boolean,
    val datetime: String,
    val group: String,
    val activity: String,
    val recordingPeriod: String?,
    val slotsCount: String?
)

fun SportsActivity.toDisplay(datetime: String, recordingPeriod: String?) =
    SportsActivityDisplay(
        hasAvailableSlots = availableSlots.let { it == null || it > 0 },
        preEntry = schedule.preEntry,
        datetime = datetime,
        group = schedule.group,
        activity = schedule.activity,
        recordingPeriod = recordingPeriod,
        slotsCount = with(schedule) {
            if (totalSlots != null && availableSlots != null) {
                "$availableSlots/$totalSlots"
            } else {
                null
            }
        }
    )
