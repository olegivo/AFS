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

import com.kaspersky.kaspresso.screens.KScreen
import io.github.kakaocup.kakao.check.KCheckBox
import io.github.kakaocup.kakao.common.assertions.BaseAssertions
import io.github.kakaocup.kakao.common.views.KView
import io.github.kakaocup.kakao.edit.KTextInputLayout
import io.github.kakaocup.kakao.text.KButton
import io.github.kakaocup.kakao.text.KTextView
import io.github.kakaocup.kakao.toolbar.KToolbar
import ru.olegivo.afs.R
import ru.olegivo.afs.schedules.presentation.models.SportsActivityDisplay

object ScheduleDetailsFragmentScreen : KScreen<ScheduleDetailsFragmentScreen>() {
    private val toolbar = KToolbar { withId(R.id.toolbar) }
    private val textViewDuty = KTextView { withId(R.id.textViewDuty) }
    private val cardViewRecord = KView { withId(R.id.cardViewRecord) }
    private val textViewRecordingTitle = KTextView { withId(R.id.textViewRecordingTitle) }
    private val textViewRecordingPeriod = KTextView { withId(R.id.textViewRecordingPeriod) }
    private val textInputLayoutFio = KTextInputLayout { withId(R.id.textInputLayoutFio) }
    private val textInputLayoutPhone = KTextInputLayout { withId(R.id.textInputLayoutPhone) }
    private val checkBoxAgreement = KCheckBox { withId(R.id.checkBoxAgreement) }
    private val textViewAgreement = KTextView { withId(R.id.textViewAgreement) }
    private val textViewSlots = KTextView { withId(R.id.textViewSlots) }
    private val textViewSlotsCaption = KTextView { withId(R.id.textViewSlotsCaption) }
    private val buttonReserve = KButton { withId(R.id.buttonReserve) }

    override val layoutId = R.layout.fragment_schedule_details
    override val viewClass = ScheduleDetailsFragment::class.java

    fun shouldDisplayDatetime(sportsActivity: SportsActivityDisplay) =
        textViewDuty.containsText(sportsActivity.datetime)

    fun shouldDisplayGroup(sportsActivity: SportsActivityDisplay) =
        toolbar.hasTitle(sportsActivity.group)

    fun shouldDisplayActivity(sportsActivity: SportsActivityDisplay) =
        toolbar.hasSubtitle(sportsActivity.activity)

    fun shouldDisplayRecording(isVisible: Boolean, sportsActivity: SportsActivityDisplay? = null) {
        cardViewRecord.shouldDisplay(isVisible)
        textViewRecordingTitle.shouldDisplay(isVisible)
        textViewRecordingPeriod.shouldDisplay(isVisible)
        textInputLayoutFio.shouldDisplay(isVisible)
        textInputLayoutPhone.shouldDisplay(isVisible)
        checkBoxAgreement.shouldDisplay(isVisible)
        textViewAgreement.shouldDisplay(isVisible)
        buttonReserve.shouldDisplay(isVisible)

        sportsActivity?.let {
            it.recordingPeriod?.let { recordingPeriod ->
                textViewRecordingPeriod.containsText(recordingPeriod)
            } ?: run { textViewRecordingPeriod.hasEmptyText() }
        }
    }

    fun shouldDisplaySlotsCount(isVisible: Boolean, sportsActivity: SportsActivityDisplay? = null) {
        sportsActivity?.let {
            textViewSlots.containsText(it.slotsCount!!)
        }
        textViewSlots.shouldDisplay(isVisible)
        textViewSlotsCaption.shouldDisplay(isVisible)
    }

    fun shouldDisplayRecordingControls(isVisible: Boolean) {
        textInputLayoutFio.shouldDisplay(isVisible)
        textInputLayoutPhone.shouldDisplay(isVisible)
        checkBoxAgreement.shouldDisplay(isVisible)
        textViewAgreement.shouldDisplay(isVisible)
        buttonReserve.shouldDisplay(isVisible)
    }
}

fun BaseAssertions.shouldDisplay(isVisible: Boolean) =
    if (isVisible) {
        isVisible()
    } else {
        isGone()
    }
