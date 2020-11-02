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

package ru.olegivo.afs.schedules.android

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.olegivo.afs.R
import ru.olegivo.afs.databinding.FragmentDayScheduleBinding
import ru.olegivo.afs.analytics.domain.ScreenNameProvider
import ru.olegivo.afs.schedules.analytics.SchedulesAnalytic
import ru.olegivo.afs.schedules.domain.models.SportsActivity
import ru.olegivo.afs.schedules.presentation.DayScheduleContract
import ru.olegivo.afs.schedules.presentation.models.Day
import java.util.Date
import javax.inject.Inject

class DayScheduleFragment @Inject constructor(
    private val presenter: DayScheduleContract.Presenter
) : Fragment(R.layout.fragment_day_schedule),
    ScreenNameProvider by SchedulesAnalytic.Screens.DaySchedule,
    DayScheduleContract.View {

    private val viewBinding: FragmentDayScheduleBinding by viewBinding(FragmentDayScheduleBinding::bind)

    override var clubId: Int = 0

    override lateinit var day: Date

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireArguments()
            .let {
                if (it.containsKey("ARG_DATE")) day = Date(it.getLong("ARG_DATE"))
                if (it.containsKey("ARG_CLUB_ID")) clubId = it.getInt("ARG_CLUB_ID")
            }
    }

    private val sportsActivitiesAdapter: SportsActivitiesAdapter by lazy {
        SportsActivitiesAdapter(requireContext(), presenter::onSportsActivityClicked)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.scheduleRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        viewBinding.scheduleRecyclerView.adapter = sportsActivitiesAdapter
        viewBinding.swipeRefresh.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light
        )
        viewBinding.swipeRefresh.setOnRefreshListener {
            presenter.actualizeSchedule()
        }
    }

    override fun onStart() {
        super.onStart()
        presenter.bindView(this)
    }

    override fun onStop() {
        presenter.unbindView()
        super.onStop()
    }

    override fun showSchedule(sportsActivities: List<SportsActivity>) {
        sportsActivitiesAdapter.items = sportsActivities.toMutableList()
    }

    override fun showErrorMessage(message: String) {
        Toast.makeText(requireContext(), "Error \n$message", Toast.LENGTH_LONG).show()
    }

    override fun showProgress() {
        viewBinding.swipeRefresh.isRefreshing = true
    }

    override fun hideProgress() {
        viewBinding.swipeRefresh.isRefreshing = false
    }

    companion object {
        fun getArguments(clubId: Int, day: Day): Bundle = bundleOf(
            "ARG_CLUB_ID" to clubId,
            "ARG_DATE" to day.date.time
        )
    }
}
