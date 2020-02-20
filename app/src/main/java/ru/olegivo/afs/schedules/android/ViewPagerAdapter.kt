package ru.olegivo.afs.schedules.android

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import ru.olegivo.afs.schedules.presentation.WeekScheduleContract

class ViewPagerAdapter(
    activity: FragmentActivity,
    private val presenter: WeekScheduleContract.Presenter
) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 7

    override fun createFragment(position: Int): Fragment {
        return DayScheduleFragment.create(
            clubId = presenter.getClubId(),
            day = presenter.getDay(position)
        )
    }
}
