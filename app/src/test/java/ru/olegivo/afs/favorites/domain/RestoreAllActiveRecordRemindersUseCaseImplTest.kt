package ru.olegivo.afs.favorites.domain

import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.willReturn
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.Test
import ru.olegivo.afs.BaseTestOf
import ru.olegivo.afs.common.domain.DateProvider
import ru.olegivo.afs.repeat
import ru.olegivo.afs.schedules.domain.ScheduleRepository
import ru.olegivo.afs.schedules.domain.models.createSchedule
import java.lang.RuntimeException
import java.util.*

class RestoreAllActiveRecordRemindersUseCaseImplTest :
    BaseTestOf<RestoreAllActiveRecordRemindersUseCase>() {

    override fun createInstance() =
        RestoreAllActiveRecordRemindersUseCaseImpl(
            scheduleRepository,
            favoritesRepository,
            dateProvider,
            scheduleReminderNotifier
        )

    //<editor-fold desc="mocks">
    private val scheduleRepository: ScheduleRepository = mock()
    private val favoritesRepository: FavoritesRepository = mock()
    private val dateProvider: DateProvider = mock()
    private val scheduleReminderNotifier: ScheduleReminderNotifier = mock()

    override fun getAllMocks() =
        arrayOf(
            scheduleRepository,
            favoritesRepository,
            dateProvider,
            scheduleReminderNotifier
        )
    //</editor-fold>

    @Test
    fun `invoke SHOWS record notifications for all active reminders WHEN no errors`() {
        val date = Date()
        val schedules = { createSchedule() }.repeat(10)
        val ids = schedules.map { it.id }

        schedules.forEach {
            given { scheduleReminderNotifier.showNotification(it) }
                .willReturn { Completable.complete() }
        }
        given { dateProvider.getDate() }.willReturn { date }
        given { favoritesRepository.getActiveRecordReminderSchedules(date) }
            .willReturn { Single.just(ids) }
        given { scheduleRepository.getSchedules(ids) }
            .willReturn { Single.just(schedules) }

        instance.invoke()
            .assertSuccess()

        verify(dateProvider).getDate()
        verify(favoritesRepository).getActiveRecordReminderSchedules(date)
        verify(scheduleRepository).getSchedules(ids)
        schedules.forEach {
            verify(scheduleReminderNotifier).showNotification(it)
        }
    }

    @Test
    fun `invoke DOES nothing WHEN errors in showing notifications`() {
        val date = Date()
        val schedules = { createSchedule() }.repeat(10)
        val ids = schedules.map { it.id }
        val exception = RuntimeException()

        schedules.forEach {
            given { scheduleReminderNotifier.showNotification(it) }
                .willReturn { Completable.error(exception) }
        }
        given { dateProvider.getDate() }.willReturn { date }
        given { favoritesRepository.getActiveRecordReminderSchedules(date) }
            .willReturn { Single.just(ids) }
        given { scheduleRepository.getSchedules(ids) }
            .willReturn { Single.just(schedules) }

        instance.invoke()
            .assertSuccess()

        verify(dateProvider).getDate()
        verify(favoritesRepository).getActiveRecordReminderSchedules(date)
        verify(scheduleRepository).getSchedules(ids)
        schedules.forEach {
            verify(scheduleReminderNotifier).showNotification(it)
        }
    }
}