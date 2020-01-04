package ru.olegivo.afs.schedules.android

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.android.support.AndroidSupportInjection
import io.reactivex.Scheduler
import kotlinx.android.synthetic.main.fragment_schedule.schedule_recycler_view
import kotlinx.android.synthetic.main.fragment_schedule.swipeRefresh
import ru.olegivo.afs.R
import ru.olegivo.afs.clubs.domain.GetCurrentClubUseCase
import ru.olegivo.afs.schedules.domain.models.SportsActivity
import ru.olegivo.afs.schedules.presentation.WeekScheduleContract
import javax.inject.Inject
import javax.inject.Named

class WeekScheduleFragment : Fragment(R.layout.fragment_schedule), WeekScheduleContract.View {
    @Inject
    lateinit var getCurrentClub: GetCurrentClubUseCase

    @Inject
    lateinit var presenter: WeekScheduleContract.Presenter

    @field:[Inject Named("main")]
    lateinit var mainScheduler: Scheduler

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    private val sportsActivitiesAdapter: SportsActivitiesAdapter by lazy {
        SportsActivitiesAdapter(requireContext(), presenter::onSportsActivityClicked)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        schedule_recycler_view.layoutManager = LinearLayoutManager(requireContext())
        schedule_recycler_view.adapter = sportsActivitiesAdapter
        swipeRefresh.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light
        )
        swipeRefresh.setOnRefreshListener {
            presenter.actualizeSchedule()
        }
    }

    override fun onStart() {
        super.onStart()
        presenter.bindView(this)
        presenter.start()
    }

    override fun onStop() {
        presenter.unbindView()
        super.onStop()
    }

    override fun showSchedule(sportsActivities: List<SportsActivity>) {
        sportsActivitiesAdapter.items = sportsActivities.toMutableList()
    }

    override fun showErrorMessage(message: String) {
        Toast.makeText(requireContext(), "Error \n${message}", Toast.LENGTH_LONG).show()
    }

    override fun showProgress() {
        swipeRefresh.isRefreshing = true
    }

    override fun hideProgress() {
        swipeRefresh.isRefreshing = false
    }
}
