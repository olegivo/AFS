package ru.olegivo.afs

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import dagger.android.support.AndroidSupportInjection
import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.fragment_main.activity_main_choose_club_button
import kotlinx.android.synthetic.main.fragment_main.activity_main_drop_db_button
import kotlinx.android.synthetic.main.fragment_main.activity_main_is_stub_reserve_check_box
import kotlinx.android.synthetic.main.fragment_main.activity_main_reserve_button
import kotlinx.android.synthetic.main.fragment_main.activity_main_set_default_club_button
import ru.olegivo.afs.clubs.android.ChooseClubDialog
import ru.olegivo.afs.clubs.domain.GetClubsUseCase
import ru.olegivo.afs.clubs.domain.GetCurrentClubUseCase
import ru.olegivo.afs.clubs.domain.SetCurrentClubUseCase
import ru.olegivo.afs.clubs.domain.models.Club
import ru.olegivo.afs.common.db.AfsDatabase
import ru.olegivo.afs.common.presentation.Navigator
import ru.olegivo.afs.schedule.domain.ReserveRepository
import ru.olegivo.afs.schedules.presentation.models.ScheduleDestination
import java.io.File
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
    lateinit var reserveRepository: ReserveRepository
    @Inject
    lateinit var navigator: Navigator
    @Inject
    lateinit var afsDatabase: AfsDatabase

    @field:[Inject Named("main")]
    lateinit var mainScheduler: Scheduler

    @field:[Inject Named("io")]
    lateinit var ioScheduler: Scheduler

    private val onCheckedChangeListener: (CompoundButton, Boolean) -> Unit = { _, isChecked ->
        reserveRepository.setStubReserve(isChecked)
            .observeOn(mainScheduler)
            .doOnSubscribe { activity_main_is_stub_reserve_check_box.isEnabled = false }
            .doFinally { activity_main_is_stub_reserve_check_box.isEnabled = true }
            .subscribeBy(
                onError = ::onError
            )
    }

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
        activity_main_set_default_club_button.setOnClickListener {
            onSetDefaultClubClicked()
        }
        activity_main_drop_db_button.setOnClickListener {
            onDropDbBClicked()
        }
    }

    override fun onStart() {
        super.onStart()
        reserveRepository.isStubReserve()
            .observeOn(mainScheduler)
            .doOnSubscribe {
                activity_main_is_stub_reserve_check_box.isEnabled = false
                activity_main_is_stub_reserve_check_box.setOnCheckedChangeListener(null)
            }
            .doFinally {
                activity_main_is_stub_reserve_check_box.isEnabled = true
                activity_main_is_stub_reserve_check_box.setOnCheckedChangeListener(
                    onCheckedChangeListener
                )
            }
            .subscribeBy(
                onSuccess = { isStubReserve ->
                    if (activity_main_is_stub_reserve_check_box.isChecked != isStubReserve) {
                        activity_main_is_stub_reserve_check_box.isChecked = isStubReserve
                    }
                },
                onError = ::onError
            )
    }

    private fun onDropDbBClicked() {
        Completable.fromCallable {
            afsDatabase.openHelper.close()
            deleteDatabaseFile(requireContext(), afsDatabase.openHelper.databaseName)
        }.subscribeOn(ioScheduler)
            .observeOn(mainScheduler)
            .subscribeBy(
                onComplete = { showMessage("БД удалена") },
                onError = ::onError
            )
    }

    private fun onSetDefaultClubClicked() {
        setCurrentClub(375)
            .observeOn(mainScheduler)
            .subscribeBy(
                onComplete = { showMessage("Выбран клуб по умолчанию") },
                onError = ::onError
            )
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
        showMessage("Error \n${t.message}")
    }

    private fun showMessage(message: String?) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    private fun deleteDatabaseFile(
        context: Context,
        databaseName: String
    ): Boolean {
        val databases = File(context.applicationInfo.dataDir + "/databases")
        val db = File(databases, databaseName)
        if (db.exists()) {
            if (db.delete()) {
                println("$databaseName database deleted")
            } else {
                println("Failed to delete $databaseName database")
                return false
            }
        }
        val journal = File(databases, "$databaseName-journal")
        if (journal.exists()) {
            if (journal.delete()) {
                println("$databaseName database journal deleted")
            } else {
                println("Failed to delete $databaseName database journal")
                return false
            }
        }

        return true
    }
}
