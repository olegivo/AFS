package ru.olegivo.afs

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import dagger.android.AndroidInjection
import io.reactivex.Scheduler
import kotlinx.android.synthetic.main.activity_main.activity_main_choose_club
import ru.olegivo.afs.clubs.android.ChooseClubDialog
import ru.olegivo.afs.clubs.domain.GetClubsUseCase
import ru.olegivo.afs.clubs.domain.GetCurrentClubUseCase
import ru.olegivo.afs.clubs.domain.SetCurrentClubUseCase
import ru.olegivo.afs.clubs.domain.models.Club
import javax.inject.Inject
import javax.inject.Named


class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var getClubs: GetClubsUseCase
    @Inject
    lateinit var getCurrentClub: GetCurrentClubUseCase
    @Inject
    lateinit var setCurrentClub: SetCurrentClubUseCase

    @field:[Inject Named("main")]
    lateinit var mainScheduler: Scheduler

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        activity_main_choose_club.setOnClickListener {
            onChooseClubClicked()
        }
    }

    private fun onChooseClubClicked() {
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
                    ChooseClubDialog.chooseClub(clubs, selectedClub, this) { club -> setCurrentClub(club) }
                },
                ::onError
            )
    }

    private fun setCurrentClub(club: Club) {
        setCurrentClub(club.id)
            .observeOn(mainScheduler)
            .subscribe(
                {
                    Toast.makeText(this, "Current club is ${club.title}", Toast.LENGTH_LONG).show()
                },
                ::onError
            )
    }

    private fun onError(t: Throwable) {
        Toast.makeText(this, "Error \n${t.message}", Toast.LENGTH_LONG).show()
    }
}
