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

package ru.olegivo.afs.schedule.android

import androidx.core.view.isVisible
import ru.olegivo.afs.databinding.FragmentScheduleDetailsBinding
import ru.olegivo.afs.schedules.presentation.models.SportsActivityDisplay

fun FragmentScheduleDetailsBinding.bind(
    sportsActivity: SportsActivityDisplay
) {
    textViewGroup.text = sportsActivity.group
    textViewActivity.text = sportsActivity.activity
    textViewDuty.text = sportsActivity.datetime
    cardViewRecord.isVisible = sportsActivity.preEntry
    sportsActivity.slotsCount?.let {
        textViewSlots.text = it
        textViewSlots.isVisible = true
        textViewSlotsCaption.isVisible = true
    }
    textViewRecordingPeriod.text = sportsActivity.recordingPeriod
    groupRecording.isVisible = sportsActivity.hasAvailableSlots
}
