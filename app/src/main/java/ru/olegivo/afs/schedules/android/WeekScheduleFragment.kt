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

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import dagger.android.support.AndroidSupportInjection
import io.reactivex.Scheduler
import kotlinx.android.synthetic.main.fragment_week_schedule.tabs
import kotlinx.android.synthetic.main.fragment_week_schedule.view_pager
import ru.olegivo.afs.R
import ru.olegivo.afs.clubs.domain.GetCurrentClubUseCase
import ru.olegivo.afs.schedules.presentation.WeekScheduleContract
import javax.inject.Inject
import javax.inject.Named

class WeekScheduleFragment : Fragment(R.layout.fragment_week_schedule), WeekScheduleContract.View {
    @Inject
    lateinit var getCurrentClub: GetCurrentClubUseCase

    @Inject
    lateinit var presenter: WeekScheduleContract.Presenter

    @field:[Inject Named("main")]
    lateinit var mainScheduler: Scheduler

    private lateinit var pagerAdapter: ViewPagerAdapter
    private lateinit var tabLayoutMediator: TabLayoutMediator

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    private val onPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            presenter.onDayChanged(position)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pagerAdapter = ViewPagerAdapter(requireActivity(), presenter)
        view_pager.adapter = pagerAdapter
        tabLayoutMediator = TabLayoutMediator(tabs, view_pager) { tab, position ->
            tab.text = presenter.getDay(position).caption
        }
    }

    override fun onStart() {
        super.onStart()
        presenter.bindView(this)
        tabLayoutMediator.attach()
    }

    override fun onStop() {
        view_pager.unregisterOnPageChangeCallback(onPageChangeCallback)
        tabLayoutMediator.detach()
        presenter.unbindView()
        super.onStop()
    }

    override fun onReady(position: Int) {
        view_pager.currentItem = position
        view_pager.registerOnPageChangeCallback(onPageChangeCallback)
    }

    override fun showErrorMessage(message: String) {
        Toast.makeText(requireContext(), "Error \n$message", Toast.LENGTH_LONG).show()
    }

    override fun showProgress() {
//        swipeRefresh.isRefreshing = true
    }

    override fun hideProgress() {
//        swipeRefresh.isRefreshing = false
    }
}
