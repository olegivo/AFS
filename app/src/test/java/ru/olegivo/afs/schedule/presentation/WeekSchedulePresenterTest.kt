package ru.olegivo.afs.schedule.presentation

import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.Single
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import ru.olegivo.afs.BaseTest
import ru.olegivo.afs.common.add
import ru.olegivo.afs.common.domain.DateProvider
import ru.olegivo.afs.common.firstDayOfWeek
import ru.olegivo.afs.common.getDateWithoutTime
import ru.olegivo.afs.helpers.capture
import ru.olegivo.afs.helpers.getRandomInt
import ru.olegivo.afs.schedule.domain.GetCurrentWeekScheduleUseCase
import ru.olegivo.afs.schedule.domain.models.Schedule
import ru.olegivo.afs.schedule.domain.models.createSchedule
import java.util.*


class WeekSchedulePresenterTest : BaseTest() {
    private val getCurrentWeekScheduleUseCase: GetCurrentWeekScheduleUseCase = mock()
    private val view: ScheduleContract.View = mock()
    private val dateProvider: DateProvider = mock()

    private val weekSchedulePresenter: ScheduleContract.Presenter = WeekSchedulePresenter(
        getCurrentWeekScheduleUseCase,
        dateProvider,
        schedulerRule.testScheduler
    )

    override fun getAllMocks() = arrayOf(
        getCurrentWeekScheduleUseCase,
        view,
        dateProvider
    )

    @Test
    fun `start shows current day schedule`() {
        val clubId = getRandomInt()
        val now = Date()
        given(dateProvider.getDate()).willReturn(now)

        val today = now.getDateWithoutTime()
        val firstDayOfWeek = firstDayOfWeek(today)

        val weekSchedule = (0..6).flatMap { dayOfWeek ->
            val scheduleDate = firstDayOfWeek.add(days = dayOfWeek)
            (1..23).map { hoursOfDay ->
                createSchedule().copy(datetime = scheduleDate.add(hours = hoursOfDay))
            }
        }

        given(getCurrentWeekScheduleUseCase.invoke(clubId)).willReturn(Single.just(weekSchedule))
        weekSchedulePresenter.bindView(view)

        weekSchedulePresenter.start(clubId)
            .andTriggerActions()

        val shownSchedules = view.capture { param: List<Schedule> -> showSchedule(param) }
        val todaySchedules = weekSchedule.filter { it.datetime.getDateWithoutTime() == today }
        assertThat(shownSchedules).containsExactlyElementsOf(todaySchedules)

        verify(getCurrentWeekScheduleUseCase).invoke(clubId)
        verify(dateProvider).getDate()
    }

}
