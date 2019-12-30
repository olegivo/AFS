package ru.olegivo.afs.favorites.domain

import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.willReturn
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.Test
import ru.olegivo.afs.BaseTestOf
import ru.olegivo.afs.helpers.getRandomLong
import ru.olegivo.afs.schedules.domain.ScheduleRepository
import ru.olegivo.afs.schedules.domain.models.createSchedule

class ShowRecordReminderUseCaseImplTest : BaseTestOf<ShowRecordReminderUseCase>() {
    override fun createInstance() =
        ShowRecordReminderUseCaseImpl(scheduleRepository, scheduleReminderNotifier)

    //<editor-fold desc="mocks">
    private val scheduleRepository: ScheduleRepository = mock()
    private val scheduleReminderNotifier: ScheduleReminderNotifier = mock()

    override fun getAllMocks() =
        arrayOf(
            scheduleRepository,
            scheduleReminderNotifier
        )
    //</editor-fold>

    @Test
    fun `invoke SHOWS record notification for schedule`() {
        val scheduleId = getRandomLong()
        val schedule = createSchedule()
        given { scheduleRepository.getSchedule(scheduleId) }
            .willReturn { Single.just(schedule) }
        given { scheduleReminderNotifier.showNotification(schedule) }
            .willReturn { Completable.complete() }

        instance.invoke(scheduleId)
            .assertSuccess()

        verify(scheduleRepository).getSchedule(scheduleId)
        verify(scheduleReminderNotifier).showNotification(schedule)
    }

}