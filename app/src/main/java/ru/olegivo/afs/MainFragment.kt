package ru.olegivo.afs

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import dagger.android.support.AndroidSupportInjection
import io.reactivex.Scheduler
import kotlinx.android.synthetic.main.fragment_main.activity_main_choose_club_button
import kotlinx.android.synthetic.main.fragment_main.activity_main_reserve_button
import ru.olegivo.afs.clubs.android.ChooseClubDialog
import ru.olegivo.afs.clubs.domain.GetClubsUseCase
import ru.olegivo.afs.clubs.domain.GetCurrentClubUseCase
import ru.olegivo.afs.clubs.domain.SetCurrentClubUseCase
import ru.olegivo.afs.clubs.domain.models.Club
import ru.olegivo.afs.common.presentation.Navigator
import ru.olegivo.afs.schedules.presentation.models.ScheduleDestination
import javax.inject.Inject
import javax.inject.Named

class MainFragment : Fragment(R.layout.fragment_main) {

    @Inject
    lateinit var getClubs: GetClubsUseCase
    @Inject
    lateinit var getCurrentClub: GetCurrentClubUseCase
    @Inject
    lateinit var setCurrentClub: SetCurrentClubUseCase
    @Inject
    lateinit var navigator: Navigator

    @field:[Inject Named("main")]
    lateinit var mainScheduler: Scheduler

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity_main_choose_club_button.setOnClickListener {
            onChooseClubClicked()
        }
        activity_main_reserve_button.setOnClickListener {
            onReserveClicked()
        }
    }

    private fun onChooseClubClicked() {
        setCurrentClub(375)
            .subscribe()
        getClubs()
            .flatMap { clubs ->
                getCurrentClub().toSingle(-1)
                    .map { currentClubId -> clubs to currentClubId }
            }
            .observeOn(mainScheduler)
            .subscribe(
                { info ->
                    val clubs = info.first

                    val selectedClub = clubs.singleOrNull {
                        val currentClubId = info.second
                        it.id == currentClubId
                    }
                    ChooseClubDialog.chooseClub(clubs, selectedClub, requireContext()) { club ->
                        setCurrentClub(
                            club
                        )
                    }
                },
                ::onError
            )
    }

    private fun onReserveClicked() {
        navigator.navigateTo(ScheduleDestination)
    }

    private fun setCurrentClub(club: Club) {
        setCurrentClub(club.id)
            .observeOn(mainScheduler)
            .subscribe(
                {
                    Toast.makeText(
                        requireContext(),
                        "Current club is ${club.title}",
                        Toast.LENGTH_LONG
                    ).show()
                },
                ::onError
            )
    }

    private fun onError(t: Throwable) {
        Toast.makeText(requireContext(), "Error \n${t.message}", Toast.LENGTH_LONG).show()
    }

}
