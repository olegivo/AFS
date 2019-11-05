package ru.olegivo.afs.reserve.android

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_reserve.buttonReserve
import kotlinx.android.synthetic.main.fragment_reserve.textInputLayoutFio
import kotlinx.android.synthetic.main.fragment_reserve.textInputLayoutPhone
import kotlinx.android.synthetic.main.fragment_reserve.textViewActivity
import kotlinx.android.synthetic.main.fragment_reserve.textViewDuty
import kotlinx.android.synthetic.main.fragment_reserve.textViewGroup
import kotlinx.android.synthetic.main.fragment_reserve.textViewSlots
import ru.olegivo.afs.R
import ru.olegivo.afs.common.presentation.Navigator
import ru.olegivo.afs.reserve.domain.models.ReserveContacts
import ru.olegivo.afs.reserve.presentation.ReserveContract
import ru.olegivo.afs.schedule.domain.models.Schedule
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class ReserveFragment : Fragment(R.layout.fragment_reserve),
    ReserveContract.View {

    private lateinit var schedule: Schedule

    @Inject
    lateinit var presenter: ReserveContract.Presenter

    @Inject
    lateinit var navigator: Navigator

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        schedule = requireArguments().toSchedule()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonReserve.setOnClickListener {
            presenter.onReserveClicked(
                schedule,
                textInputLayoutFio.editText!!.text.toString(),
                textInputLayoutPhone.editText!!.text.toString()
            )
        }
    }

    override fun onStart() {
        super.onStart()
        presenter.bindView(this)
        presenter.start(schedule)
    }

    override fun onStop() {
        presenter.unbindView()
        presenter.saveReserveContacts(
            ReserveContacts(
                textInputLayoutFio.editText!!.text.toString(),
                textInputLayoutPhone.editText!!.text.toString()
            )
        )
        super.onStop()
    }

    override fun showScheduleToReserve(schedule: Schedule) {
        textViewGroup.text = schedule.group
        textViewActivity.text = schedule.activity
        textViewDuty.text = hoursMinutesFormat.format(schedule.datetime)
        schedule.totalSlots?.let {
            textViewSlots.text =
                requireContext().getString(
                    R.string.slots_count,
                    schedule.availableSlots,
                    schedule.totalSlots
                )
        } ?: run {
            textViewSlots.visibility = View.GONE
        }
    }

    override fun showSuccessReserved() {
        showExitMessage("Вы записаны на занятие")
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
        fun createInstance(schedule: Schedule): ReserveFragment {
            return ReserveFragment().apply {
                arguments = schedule.toBundle()
            }
        }

        private const val FORMAT = "HH:mm"

        private val hoursMinutesFormat: SimpleDateFormat by lazy {
            // TODO: copy paste
            SimpleDateFormat(FORMAT, Locale.getDefault())
        }

    }
}

private object Fields {
    const val id = "Schedule.id"
    const val clubId = "Schedule.clubId"
    const val availableSlots = "Schedule.availableSlots"
    const val datetime = "Schedule.datetime"
    const val totalSlots = "Schedule.totalSlots"
    const val activity = "Schedule.activity"
    const val group = "Schedule.group"
    const val length = "Schedule.length"
    const val preEntry = "Schedule.preEntry"
    const val room = "Schedule.room"
    const val trainer = "Schedule.trainer"
    const val isReserved = "Schedule.isReserved"

}


private fun Schedule.toBundle(): Bundle {
    return Bundle().apply {
        putLong(Fields.id, id)
        putInt(Fields.clubId, clubId)
        availableSlots?.let { putInt(Fields.availableSlots, it) }
        totalSlots?.let { putInt(Fields.totalSlots, it) }
        putLong(Fields.datetime, datetime.time)
        putString(Fields.activity, activity)
        putString(Fields.group, group)
        putInt(Fields.length, length)
        putBoolean(Fields.preEntry, preEntry)
        room?.let { putString(Fields.room, it) }
        trainer?.let { putString(Fields.trainer, it) }
        putBoolean(Fields.isReserved, isReserved)
    }
}

private fun Bundle.toSchedule(): Schedule {
    return Schedule(
        id = requireLong(Fields.id),
        clubId = requireInt(Fields.clubId),
        availableSlots = getIntOrNull(Fields.availableSlots),
        totalSlots = getIntOrNull(Fields.totalSlots),
        datetime = requireDate(Fields.datetime),
        activity = requireString(Fields.activity),
        group = requireString(Fields.group),
        length = requireInt(Fields.length),
        preEntry = requireBoolean(Fields.preEntry),
        room = getStringOrNull(Fields.room),
        trainer = getStringOrNull(Fields.trainer),
        isReserved = requireBoolean(Fields.isReserved)
    )
}

private fun Bundle.getIntOrNull(key: String): Int? = getOrNull(key) { getInt(it) }
private fun Bundle.getStringOrNull(key: String): String? = getOrNull(key) { getString(it) }
private fun Bundle.requireInt(key: String): Int = require(key) { getInt(it) }
private fun Bundle.requireLong(key: String): Long = require(key) { getLong(it) }
private fun Bundle.requireDate(key: String): Date = Date(requireLong(key))
private fun Bundle.requireString(key: String): String = require(key) { getString(it)!! }
private fun Bundle.requireBoolean(key: String): Boolean = require(key) { getBoolean(it) }

private inline fun <T : Any> Bundle.getOrNull(key: String, getter: Bundle.(String) -> T): T? =
    if (containsKey(key)) {
        getter(key)
    } else {
        null
    }

private inline fun <T : Any> Bundle.require(key: String, getter: Bundle.(String) -> T): T =
    if (containsKey(key)) {
        getter(key)
    } else {
        throw IndexOutOfBoundsException("Cannot find value for key $key")
    }
