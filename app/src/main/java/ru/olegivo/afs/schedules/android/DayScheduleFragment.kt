package ru.olegivo.afs.schedules.android

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.android.support.AndroidSupportInjection
import io.reactivex.Scheduler
import kotlinx.android.synthetic.main.fragment_day_schedule.schedule_recycler_view
import kotlinx.android.synthetic.main.fragment_day_schedule.swipeRefresh
import ru.olegivo.afs.R
import ru.olegivo.afs.clubs.domain.GetCurrentClubUseCase
import ru.olegivo.afs.schedules.domain.models.SportsActivity
import ru.olegivo.afs.schedules.presentation.DayScheduleContract
import ru.olegivo.afs.schedules.presentation.models.Day
import java.util.*
import javax.inject.Inject
import javax.inject.Named

class DayScheduleFragment : Fragment(R.layout.fragment_day_schedule), DayScheduleContract.View {
    @Inject
    lateinit var getCurrentClub: GetCurrentClubUseCase
    @Inject
    lateinit var presenter: DayScheduleContract.Presenter

    @field:[Inject Named("main")]
    lateinit var mainScheduler: Scheduler

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
        swipeRefresh.isRefreshing = true
    }

    override fun hideProgress() {
        swipeRefresh.isRefreshing = false
    }

    companion object {
        fun create(clubId: Int, day: Day) =
            DayScheduleFragment().apply {
                arguments = bundleOf(
                    "ARG_CLUB_ID" to clubId,
                    "ARG_DATE" to day.date.time
                )
            }
    }
}
