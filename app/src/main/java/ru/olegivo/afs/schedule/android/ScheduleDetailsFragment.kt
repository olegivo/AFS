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

package ru.olegivo.afs.schedule.android

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_schedule_details.buttonReserve
import kotlinx.android.synthetic.main.fragment_schedule_details.checkBoxAgreement
import kotlinx.android.synthetic.main.fragment_schedule_details.imageViewIsFavorite
import kotlinx.android.synthetic.main.fragment_schedule_details.textInputLayoutFio
import kotlinx.android.synthetic.main.fragment_schedule_details.textInputLayoutPhone
import kotlinx.android.synthetic.main.fragment_schedule_details.textViewActivity
import kotlinx.android.synthetic.main.fragment_schedule_details.textViewAgreement
import kotlinx.android.synthetic.main.fragment_schedule_details.textViewDuty
import kotlinx.android.synthetic.main.fragment_schedule_details.textViewGroup
import kotlinx.android.synthetic.main.fragment_schedule_details.textViewSlots
import ru.olegivo.afs.R
import ru.olegivo.afs.common.presentation.Navigator
import ru.olegivo.afs.schedule.domain.models.ReserveContacts
import ru.olegivo.afs.schedule.presentation.ScheduleDetailsContract
import ru.olegivo.afs.schedules.domain.models.SportsActivity
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

class ScheduleDetailsFragment @Inject constructor(
    private val presenter: ScheduleDetailsContract.Presenter,
    private val navigator: Navigator
) :
    Fragment(R.layout.fragment_schedule_details),
    ScheduleDetailsContract.View {

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
        requireArguments().toArgs().also {
            presenter.init(it.id, it.clubId)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageViewIsFavorite

        buttonReserve.setOnClickListener {
            presenter.onReserveClicked(
                checkBoxAgreement.isChecked
            )
        }
        imageViewIsFavorite.setOnClickListener {
            presenter.onFavoriteClick()
        }
        textViewAgreement.setOnClickListener {
            presenter.onAgreementClicked()
        }
    }

    override fun isAgreementAccepted() = checkBoxAgreement.isChecked

    override fun getReserveContacts() =
        ReserveContacts(
            fio = textInputLayoutFio.editText!!.text.toString(),
            phone = textInputLayoutPhone.editText!!.text.toString()
        )

    override fun showIsFavorite(isFavorite: Boolean) {
        imageViewIsFavorite.setImageResource(if (isFavorite) R.drawable.ic_favorite_black_24dp else R.drawable.ic_favorite_border_black_24dp)
    }

    override fun onStart() {
        super.onStart()
        presenter.bindView(this)
    }

    override fun onStop() {
        presenter.unbindView()
        super.onStop()
    }

    override fun showScheduleToReserve(sportsActivity: SportsActivity) {
        textViewGroup.text = sportsActivity.schedule.group
        textViewActivity.text = sportsActivity.schedule.activity
        textViewDuty.text = hoursMinutesFormat.format(sportsActivity.schedule.datetime)
        sportsActivity.schedule.totalSlots?.let {
            textViewSlots.text =
                requireContext().getString(
                    R.string.slots_count,
                    sportsActivity.availableSlots,
                    sportsActivity.schedule.totalSlots
                )
        } ?: run {
            textViewSlots.visibility = View.GONE
        }
    }

    override fun showSuccessReserved() {
        showExitMessage("Вы записаны на занятие")
    }

    override fun showHaveToAcceptAgreement() {
        Snackbar.make(
            requireView(),
            "Необходимо согласиться с обработкой персональных данных",
            Snackbar.LENGTH_LONG
        ).show()
    }

    override fun showTryLater() {
        Snackbar.make(
            requireView(),
            "Произошла ошибка. Попробуйте повторить запрос позже",
            Snackbar.LENGTH_LONG
        ).show()
    }

    override fun showNameAndPhoneShouldBeStated() {
        Snackbar.make(
            requireView(),
            "Необходимо указать ФИО и телефон",
            Snackbar.LENGTH_LONG
        ).show()
    }

    override fun setReserveContacts(reserveContacts: ReserveContacts) {
        textInputLayoutFio.editText!!.setText(reserveContacts.fio)
        textInputLayoutPhone.editText!!.setText(reserveContacts.phone)
    }

    override fun setAgreementAccepted() {
        checkBoxAgreement.isChecked = true
    }

    override fun showTheTimeHasGone() {
        showExitMessage("Вы не можете записаться на занятие, т.к. время начала уже прошло")
    }

    override fun showHasNoSlotsAPriori() {
        showExitMessage("Не осталось свободных мест")
    }

    override fun showHasNoSlotsAPosteriori() {
        showExitMessage("Не осталось свободных мест. Места закончились до того, как вы отправили запрос.")
    }

    override fun showAlreadyReserved() {
        showExitMessage("Вы уже записывались на это занятие")
    }

    private fun showExitMessage(message: String) {
        buttonReserve.isEnabled = false
        Snackbar.make(
            requireView(),
            message,
            Snackbar.LENGTH_INDEFINITE
        )
            .setAction("OK") { navigator.navigateBack() }
            .show()
    }

    @SuppressLint("ConstantLocale")
    companion object {
        fun getArguments(id: Long, clubId: Int) = Args(id, clubId).toBundle()

        private const val FORMAT = "HH:mm"

        private val hoursMinutesFormat: SimpleDateFormat by lazy {
            // TODO: copy paste
            SimpleDateFormat(FORMAT, Locale.getDefault())
        }
    }

    data class Args(val id: Long, val clubId: Int) {
        object Fields {
            const val id = "Schedule.id"
            const val clubId = "Schedule.clubId"
        }
    }
}

private fun ScheduleDetailsFragment.Args.toBundle(): Bundle {
    return Bundle().apply {
        putLong(ScheduleDetailsFragment.Args.Fields.id, id)
        putInt(ScheduleDetailsFragment.Args.Fields.clubId, clubId)
    }
}

private fun Bundle.toArgs(): ScheduleDetailsFragment.Args {
    return ScheduleDetailsFragment.Args(
        id = requireLong(ScheduleDetailsFragment.Args.Fields.id),
        clubId = requireInt(ScheduleDetailsFragment.Args.Fields.clubId)
    )
}

private fun Bundle.requireInt(key: String): Int = require(key) { getInt(it) }
private fun Bundle.requireLong(key: String): Long = require(key) { getLong(it) }

private inline fun <T : Any> Bundle.require(key: String, getter: Bundle.(String) -> T): T =
    if (containsKey(key)) {
        getter(key)
    } else {
        throw IndexOutOfBoundsException("Cannot find value for key $key")
    }
