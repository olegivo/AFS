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

package ru.olegivo.afs.home.android

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.rxkotlin.subscribeBy
import ru.olegivo.afs.R
import ru.olegivo.afs.analytics.domain.ScreenNameProvider
import ru.olegivo.afs.clubs.android.ChooseClubDialog
import ru.olegivo.afs.clubs.domain.GetClubsUseCase
import ru.olegivo.afs.clubs.domain.GetCurrentClubUseCase
import ru.olegivo.afs.clubs.domain.SetCurrentClubUseCase
import ru.olegivo.afs.clubs.domain.models.Club
import ru.olegivo.afs.common.db.AfsDatabase
import ru.olegivo.afs.common.domain.ErrorReporter
import ru.olegivo.afs.common.presentation.Navigator
import ru.olegivo.afs.databinding.FragmentHomeBinding
import ru.olegivo.afs.favorites.presentation.models.FavoritesDestination
import ru.olegivo.afs.schedule.domain.ReserveRepository
import ru.olegivo.afs.schedules.presentation.models.ScheduleDestination
import java.io.File
import javax.inject.Inject
import javax.inject.Named

class HomeFragment @Inject constructor(
    private val errorReporter: ErrorReporter,
    private val reserveRepository: ReserveRepository,
    private val getClubs: GetClubsUseCase,
    private val getCurrentClub: GetCurrentClubUseCase,
    private val setCurrentClub: SetCurrentClubUseCase,
    private val navigator: Navigator,
    private val afsDatabase: AfsDatabase,
    @Named("main") private val mainScheduler: Scheduler,
    @Named("io") private val ioScheduler: Scheduler
) : Fragment(R.layout.fragment_home),
    ScreenNameProvider {

    private val viewBinding: FragmentHomeBinding by viewBinding(FragmentHomeBinding::bind)

    private var isStubReserve: Boolean? = null

    private val onCheckedChangeListener: (CompoundButton, Boolean) -> Unit = { _, isChecked ->
        reserveRepository.setStubReserve(isChecked)
            .observeOn(mainScheduler)
            .doOnSubscribe { viewBinding.activityMainIsStubReserveCheckBox.isEnabled = false }
            .doFinally { viewBinding.activityMainIsStubReserveCheckBox.isEnabled = true }
            .subscribeBy(
                onError = ::onError
            )
    }

    override val screenName: String = "home"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewBinding.activityMainChooseClubButton.setOnClickListener {
            onChooseClubClicked()
        }
        viewBinding.activityMainFavoritesButton.setOnClickListener {
            onFavoritesClicked()
        }
        viewBinding.activityMainReserveButton.setOnClickListener {
            onReserveClicked()
        }
        viewBinding.activityMainSetDefaultClubButton.setOnClickListener {
            onSetDefaultClubClicked()
        }
        viewBinding.activityMainDropDbButton.setOnClickListener {
            onDropDbBClicked()
        }
        isStubReserve?.let { isStubReserve ->
            initIsStubReserve(isStubReserve)
        } ?: run {
            disableIsStubReserveCheckBox()
        }
    }

    override fun onStart() {
        super.onStart()
        reserveRepository.isStubReserve()
            .observeOn(mainScheduler)
            .subscribeBy(
                onSuccess = { isStubReserve ->
                    this.isStubReserve = isStubReserve
                    if (viewBinding.activityMainIsStubReserveCheckBox != null) {
                        initIsStubReserve(isStubReserve)
                    }
                },
                onError = ::onError
            )
    }

    private fun initIsStubReserve(isStubReserve: Boolean) {
        if (viewBinding.activityMainIsStubReserveCheckBox.isChecked != isStubReserve) {
            viewBinding.activityMainIsStubReserveCheckBox.isChecked = isStubReserve
        }
        viewBinding.activityMainIsStubReserveCheckBox.isEnabled = true
        viewBinding.activityMainIsStubReserveCheckBox.setOnCheckedChangeListener(
            onCheckedChangeListener
        )
    }

    private fun disableIsStubReserveCheckBox() {
        viewBinding.activityMainIsStubReserveCheckBox.isEnabled = false
        viewBinding.activityMainIsStubReserveCheckBox.setOnCheckedChangeListener(null)
    }

    private fun onDropDbBClicked() {
        Completable.fromCallable {
            afsDatabase.openHelper.close()
            deleteDatabaseFile(requireContext(), afsDatabase.openHelper.databaseName!!)
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

    private fun onFavoritesClicked() {
        navigator.navigateTo(FavoritesDestination)
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
        errorReporter.reportError(t, t.message ?: "")
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
