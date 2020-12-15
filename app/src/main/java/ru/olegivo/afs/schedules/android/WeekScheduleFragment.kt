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
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.tabs.TabLayoutMediator
import ru.olegivo.afs.R
import ru.olegivo.afs.analytics.domain.ScreenNameProvider
import ru.olegivo.afs.common.android.doOnApplyWindowInsets
import ru.olegivo.afs.databinding.FragmentWeekScheduleBinding
import ru.olegivo.afs.schedules.analytics.SchedulesAnalytic
import ru.olegivo.afs.schedules.presentation.WeekScheduleContract
import javax.inject.Inject
import javax.inject.Provider

class WeekScheduleFragment @Inject constructor(
    private val presenter: WeekScheduleContract.Presenter,
    private val dayScheduleFragmentProvider: Provider<DayScheduleFragment>
) : Fragment(R.layout.fragment_week_schedule),
    ScreenNameProvider by SchedulesAnalytic.Screens.WeekSchedule,
    WeekScheduleContract.View {

    private val viewBinding: FragmentWeekScheduleBinding by viewBinding(FragmentWeekScheduleBinding::bind)

    private lateinit var pagerAdapter: ViewPagerAdapter
    private lateinit var tabLayoutMediator: TabLayoutMediator

    private val onPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            presenter.onDayChanged(position)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewBinding.toolbarLayout.toolbar.title = "Schedules"

        pagerAdapter = ViewPagerAdapter(requireActivity(), presenter, dayScheduleFragmentProvider)
        viewBinding.viewPager.adapter = pagerAdapter
        tabLayoutMediator =
            TabLayoutMediator(viewBinding.tabs, viewBinding.viewPager) { tab, position ->
                tab.text = presenter.getDay(position).caption
            }

        viewBinding.toolbarLayout.appbarLayout.doOnApplyWindowInsets { view, insets, padding ->
            view.updatePadding(
                top = padding.top + insets.systemWindowInsetTop
            )
            insets
        }
    }

    override fun onStart() {
        super.onStart()
        presenter.bindView(this)
        tabLayoutMediator.attach()
    }

    override fun onStop() {
        viewBinding.viewPager.unregisterOnPageChangeCallback(onPageChangeCallback)
        tabLayoutMediator.detach()
        presenter.unbindView()
        super.onStop()
    }

    override fun onReady(position: Int) {
        viewBinding.viewPager.currentItem = position
        viewBinding.viewPager.registerOnPageChangeCallback(onPageChangeCallback)
    }

    override fun showErrorMessage(message: String) {
        Toast.makeText(requireContext(), "Error \n$message", Toast.LENGTH_LONG).show()
    }

    override fun showProgress() {
//        viewBinding.swipeRefresh.isRefreshing = true
    }

    override fun hideProgress() {
//        viewBinding.swipeRefresh.isRefreshing = false
    }
}
