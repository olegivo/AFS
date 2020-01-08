package ru.olegivo.afs.schedules.presentation

import io.reactivex.Scheduler
import io.reactivex.rxkotlin.subscribeBy
import org.jetbrains.annotations.TestOnly
import ru.olegivo.afs.clubs.domain.GetCurrentClubUseCase
import ru.olegivo.afs.common.add
import ru.olegivo.afs.common.domain.DateProvider
import ru.olegivo.afs.common.domain.ErrorReporter
import ru.olegivo.afs.common.firstDayOfWeek
import ru.olegivo.afs.common.get
import ru.olegivo.afs.common.presentation.BasePresenter
import ru.olegivo.afs.common.presentation.Navigator
import ru.olegivo.afs.schedule.presentation.models.ReserveDestination
import ru.olegivo.afs.schedules.domain.ActualizeScheduleUseCase
import ru.olegivo.afs.schedules.domain.models.SportsActivity
import ru.olegivo.afs.schedules.presentation.models.Day
import java.util.*
import javax.inject.Inject
import javax.inject.Named

class WeekSchedulePresenter @Inject constructor(
    private val getCurrentClub: GetCurrentClubUseCase,
    private val actualizeSchedule: ActualizeScheduleUseCase,
    private val dateProvider: DateProvider,
    private val navigator: Navigator,
    @Named("main") private val mainScheduler: Scheduler,
    errorReporter: ErrorReporter
) : BasePresenter<WeekScheduleContract.View>(errorReporter),
    WeekScheduleContract.Presenter {

    private var clubId = 0
    private var currentDay: Int = -1
    private lateinit var days: List<Day>

    override fun bindView(view: WeekScheduleContract.View) {
        super.bindView(view)
        if (clubId == 0) {
            start()
        } else {
            showResult()
        }
    }

    override fun getClubId(): Int = clubId

    override fun getDay(position: Int): Day = days[position]

    override fun onDayChanged(position: Int) {
        currentDay = position
    }

    override fun onSportsActivityClicked(sportsActivity: SportsActivity) {
        navigator.navigateTo(
            ReserveDestination(
                sportsActivity.schedule.id,
                sportsActivity.schedule.clubId
            )
        )
    }

    @TestOnly
    internal fun clear() {
        clubId = 0
        currentDay = -1
    }

    private fun start() {
        val now = dateProvider.getDate()
        currentDay = now.get(Calendar.DAY_OF_WEEK) - 2
        days = firstDayOfWeek(now)
            .let { firstDayOfWeek ->
                (0..6).map {
                    val weekDay = firstDayOfWeek.add(days = it)
                    Day(
                        caption = when (it) {
                            0 -> "ПН"
                            1 -> "ВТ"
                            2 -> "СР"
                            3 -> "ЧТ"
                            4 -> "ПТ"
                            5 -> "СБ"
                            6 -> "ВС"
                            else -> TODO("unknown day of week")
                        },
                        date = weekDay
                    )
                }
            }

        getCurrentClub()
            .doOnSuccess { clubId = it }
            .map { Unit }
            .observeOn(mainScheduler)
            .doOnSubscribe { view?.showProgress() }
            .doFinally { view?.hideProgress() }
            .subscribeBy(
                onSuccess = { showResult() },
                onError = {
                    onError(it, "Ошибка при получении текущего клуба")
                }
            )
            .addToComposite()
    }

    private fun showResult() {
        view?.onReady(currentDay)
    }
}

