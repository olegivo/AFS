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

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.reset
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import org.junit.Test
import ru.olegivo.afs.BaseTest
import ru.olegivo.afs.R
import ru.olegivo.afs.common.presentation.Navigator
import ru.olegivo.afs.helpers.getRandomInt
import ru.olegivo.afs.helpers.getRandomLong
import ru.olegivo.afs.helpers.getRandomString
import ru.olegivo.afs.schedule.presentation.ScheduleDetailsContract
import ru.olegivo.afs.schedules.presentation.models.SportsActivityDisplay

class ScheduleDetailsFragmentIsolatedTest : BaseTest() {

    override fun getAllMocks(): Array<Any> = arrayOf(
        presenter,
        navigator
    )

    private val presenter: ScheduleDetailsContract.Presenter = mock()
    private val navigator: Navigator = mock()

    @Test
    fun showScheduleToReserve_DISPLAYS_recording_WHEN_preEntry() {
        val fragmentScenario = launch()
        val sportsActivity = SportsActivityDisplay(
            hasAvailableSlots = true,
            preEntry = true,
            datetime = getRandomString(prefix = "datetime"),
            group = getRandomString(prefix = "group"),
            activity = getRandomString(prefix = "activity"),
            recordingPeriod = getRandomString(prefix = "recordingPeriod"),
            slotsCount = null
        )
        showScheduleToReserve(fragmentScenario, sportsActivity) {
            ScheduleDetailsFragmentScreen {
                shouldDisplayRecording(true, sportsActivity)
            }
        }
    }

    @Test
    fun showScheduleToReserve_NOT_DISPLAYS_recording_WHEN_not_preEntry() {
        val fragmentScenario = launch()
        val sportsActivity = SportsActivityDisplay(
            hasAvailableSlots = true,
            preEntry = false,
            datetime = getRandomString(prefix = "datetime"),
            group = getRandomString(prefix = "group"),
            activity = getRandomString(prefix = "activity"),
            recordingPeriod = getRandomString(prefix = "recordingPeriod"),
            slotsCount = getRandomString(prefix = "slotsCount")
        )
        showScheduleToReserve(fragmentScenario, sportsActivity) {
            ScheduleDetailsFragmentScreen {
                shouldDisplayRecording(false)
            }
        }
    }

    @Test
    fun showScheduleToReserve_DISPLAYS_record_controls_WHEN_preEntry_and_has_available_slots() {
        val fragmentScenario = launch()
        val sportsActivity = SportsActivityDisplay(
            hasAvailableSlots = true,
            preEntry = true,
            datetime = getRandomString(prefix = "datetime"),
            group = getRandomString(prefix = "group"),
            activity = getRandomString(prefix = "activity"),
            recordingPeriod = null,
            slotsCount = null
        )
        showScheduleToReserve(fragmentScenario, sportsActivity) {
            ScheduleDetailsFragmentScreen {
                shouldDisplayRecordingControls(true)
            }
        }
    }

    @Test
    fun showScheduleToReserve_NOT_DISPLAYS_record_controls_WHEN_preEntry_and_has_no_available_slots() {
        val fragmentScenario = launch()
        val sportsActivity = SportsActivityDisplay(
            hasAvailableSlots = false,
            preEntry = true,
            datetime = getRandomString(prefix = "datetime"),
            group = getRandomString(prefix = "group"),
            activity = getRandomString(prefix = "activity"),
            recordingPeriod = null,
            slotsCount = null
        )
        showScheduleToReserve(fragmentScenario, sportsActivity) {
            ScheduleDetailsFragmentScreen {
                shouldDisplayRecordingControls(false)
            }
        }
    }

    @Test
    fun showScheduleToReserve_NOT_DISPLAYS_record_controls_WHEN_not_preEntry_and_has_available_slots() {
        val fragmentScenario = launch()
        val sportsActivity = SportsActivityDisplay(
            hasAvailableSlots = true,
            preEntry = false,
            datetime = getRandomString(prefix = "datetime"),
            group = getRandomString(prefix = "group"),
            activity = getRandomString(prefix = "activity"),
            recordingPeriod = null,
            slotsCount = null
        )
        showScheduleToReserve(fragmentScenario, sportsActivity) {
            ScheduleDetailsFragmentScreen {
                shouldDisplayRecordingControls(false)
            }
        }
    }

    @Test
    fun showScheduleToReserve_DISPLAYS_slotsCount_WHEN_has_slotsCount() {
        val fragmentScenario = launch()
        val sportsActivity = SportsActivityDisplay(
            hasAvailableSlots = false,
            preEntry = false,
            datetime = getRandomString(prefix = "datetime"),
            group = getRandomString(prefix = "group"),
            activity = getRandomString(prefix = "activity"),
            recordingPeriod = null,
            slotsCount = getRandomString(prefix = "slotsCount")
        )
        showScheduleToReserve(fragmentScenario, sportsActivity) {
            ScheduleDetailsFragmentScreen {
                shouldDisplaySlotsCount(true, sportsActivity)
            }
        }
    }

    @Test
    fun showScheduleToReserve_NOT_DISPLAYS_slotsCount_WHEN_has_no_slotsCount() {
        val fragmentScenario = launch()
        val sportsActivity = SportsActivityDisplay(
            hasAvailableSlots = false,
            preEntry = false,
            datetime = getRandomString(prefix = "datetime"),
            group = getRandomString(prefix = "group"),
            activity = getRandomString(prefix = "activity"),
            recordingPeriod = null,
            slotsCount = null
        )
        showScheduleToReserve(fragmentScenario, sportsActivity) {
            ScheduleDetailsFragmentScreen {
                shouldDisplaySlotsCount(false)
            }
        }
    }

    @Test
    fun showScheduleToReserve_DISPLAYS_datetime_group_activity() {
        val fragmentScenario = launch()
        val sportsActivity = SportsActivityDisplay(
            hasAvailableSlots = false,
            preEntry = false,
            datetime = getRandomString(prefix = "datetime"),
            group = getRandomString(prefix = "group"),
            activity = getRandomString(prefix = "activity"),
            recordingPeriod = null,
            slotsCount = null
        )
        showScheduleToReserve(fragmentScenario, sportsActivity) {
            ScheduleDetailsFragmentScreen {
                shouldDisplayDatetime(sportsActivity)
                shouldDisplayGroup(sportsActivity)
                shouldDisplayActivity(sportsActivity)
            }
        }
    }

    private fun launch(): FragmentScenario<ScheduleDetailsFragment> {
        val args = ScheduleDetailsFragment.Args(
            id = getRandomLong(),
            clubId = getRandomInt()
        )
        val fragment = ScheduleDetailsFragment(
            presenter = presenter,
            navigator = navigator
        )

        val scenario = launchFragmentInContainer(
            args.toBundle(),
            R.style.AppTheme
        ) { fragment }

        verify(presenter).init(args.id, args.clubId)
        verify(presenter).bindView(fragment)
        verifyNoMoreInteractions(presenter)
        reset(presenter)

        return scenario
    }

    private fun showScheduleToReserve(
        fragmentScenario: FragmentScenario<ScheduleDetailsFragment>,
        sportsActivity: SportsActivityDisplay,
        check: () -> Unit
    ) {
        fragmentScenario.onFragment { fragment ->
            val view = fragment as ScheduleDetailsContract.View
            view.showScheduleToReserve(sportsActivity)
        }
        check()
    }
}
