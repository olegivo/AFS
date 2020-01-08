package ru.olegivo.afs.favorites.domain

import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.willReturn
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import org.junit.Test
import ru.olegivo.afs.BaseTestOf
import ru.olegivo.afs.common.domain.DateProvider
import ru.olegivo.afs.common.domain.ErrorReporter
import ru.olegivo.afs.repeat
import ru.olegivo.afs.schedule.domain.ReserveRepository
import ru.olegivo.afs.schedules.domain.ScheduleRepository
import ru.olegivo.afs.schedules.domain.models.createReserveContacts
import ru.olegivo.afs.schedules.domain.models.createSchedule
import java.util.*

class RestoreAllActiveRecordRemindersUseCaseImplTest :
    BaseTestOf<RestoreAllActiveRecordRemindersUseCase>() {

    override fun createInstance() =
        RestoreAllActiveRecordRemindersUseCaseImpl(
            scheduleRepository,
            favoritesRepository,
            dateProvider,
            scheduleReminderNotifier,
            reserveRepository,
            errorReporter
        )

    //<editor-fold desc="mocks">
    private val scheduleRepository: ScheduleRepository = mock()
    private val favoritesRepository: FavoritesRepository = mock()
    private val reserveRepository: ReserveRepository = mock()
    private val dateProvider: DateProvider = mock()
    private val scheduleReminderNotifier: ScheduleReminderNotifier = mock()
    private val errorReporter: ErrorReporter = mock()

    override fun getAllMocks() =
        arrayOf(
            scheduleRepository,
            favoritesRepository,
            reserveRepository,
            dateProvider,
            scheduleReminderNotifier,
            errorReporter
        )
    //</editor-fold>

    @Test
    fun `invoke SHOWS record notifications to show details for all active reminders WHEN agreement not accepted`() {
        val date = Date()
        val schedules = { createSchedule() }.repeat(10)
        val ids = schedules.map { it.id }

        schedules.forEach {
            given { scheduleReminderNotifier.showNotificationToShowDetails(it) }
                .willReturn { Completable.complete() }
        }
        given { dateProvider.getDate() }.willReturn { date }
        given { favoritesRepository.getActiveRecordReminderSchedules(date) }
            .willReturn { Single.just(ids) }
        given { scheduleRepository.getSchedules(ids) }
            .willReturn { Single.just(schedules) }
        given { reserveRepository.isAgreementAccepted() }
            .willReturn { Single.just(false) }

        instance.invoke()
            .assertSuccess()

        verify(dateProvider).getDate()
        verify(favoritesRepository).getActiveRecordReminderSchedules(date)
        verify(scheduleRepository).getSchedules(ids)
        verify(reserveRepository).isAgreementAccepted()
        schedules.forEach {
            verify(scheduleReminderNotifier).showNotificationToShowDetails(it)
        }
    }

    @Test
    fun `invoke SHOWS record notifications to show details for all active reminders WHEN agreement accepted, has no reserve contacts`() {
        val date = Date()
        val schedules = { createSchedule() }.repeat(10)
        val ids = schedules.map { it.id }

        schedules.forEach {
            given { scheduleReminderNotifier.showNotificationToShowDetails(it) }
                .willReturn { Completable.complete() }
        }
        given { dateProvider.getDate() }.willReturn { date }
        given { favoritesRepository.getActiveRecordReminderSchedules(date) }
            .willReturn { Single.just(ids) }
        given { scheduleRepository.getSchedules(ids) }
            .willReturn { Single.just(schedules) }
        given { reserveRepository.isAgreementAccepted() }
            .willReturn { Single.just(true) }
        given { reserveRepository.getReserveContacts() }
            .willReturn { Maybe.empty() }

        instance.invoke()
            .assertSuccess()

        verify(dateProvider).getDate()
        verify(favoritesRepository).getActiveRecordReminderSchedules(date)
        verify(scheduleRepository).getSchedules(ids)
        verify(reserveRepository).isAgreementAccepted()
        verify(reserveRepository).getReserveContacts()
        schedules.forEach {
            verify(scheduleReminderNotifier).showNotificationToShowDetails(it)
        }
    }

    @Test
    fun `invoke SHOWS notifications to reserve for all active reminders WHEN agreement accepted, has reserve contacts`() {
        val date = Date()
        val schedules = { createSchedule() }.repeat(10)
        val ids = schedules.map { it.id }
        val reserveContacts = createReserveContacts()

        schedules.forEach {
            given {
                scheduleReminderNotifier.showNotificationToReserve(
                    it,
                    reserveContacts.fio,
                    reserveContacts.phone
                )
            }
                .willReturn { Completable.complete() }
        }
        given { dateProvider.getDate() }.willReturn { date }
        given { favoritesRepository.getActiveRecordReminderSchedules(date) }
            .willReturn { Single.just(ids) }
        given { scheduleRepository.getSchedules(ids) }
            .willReturn { Single.just(schedules) }
        given { reserveRepository.isAgreementAccepted() }
            .willReturn { Single.just(true) }
        given { reserveRepository.getReserveContacts() }
            .willReturn { Maybe.just(reserveContacts) }

        instance.invoke()
            .assertSuccess()

        verify(dateProvider).getDate()
        verify(favoritesRepository).getActiveRecordReminderSchedules(date)
        verify(scheduleRepository).getSchedules(ids)
        verify(reserveRepository).isAgreementAccepted()
        verify(reserveRepository).getReserveContacts()
        schedules.forEach {
            verify(scheduleReminderNotifier).showNotificationToReserve(
                it,
                reserveContacts.fio,
                reserveContacts.phone
            )
        }
    }

    @Test
    fun `invoke DOES nothing WHEN errors in showing notifications, agreement not accepted`() {
        val date = Date()
        val schedules = { createSchedule() }.repeat(10)
        val ids = schedules.map { it.id }
        val exception = RuntimeException()

        schedules.forEach {
            given { scheduleReminderNotifier.showNotificationToShowDetails(it) }
                .willReturn { Completable.error(exception) }
        }
        given { dateProvider.getDate() }.willReturn { date }
        given { favoritesRepository.getActiveRecordReminderSchedules(date) }
            .willReturn { Single.just(ids) }
        given { scheduleRepository.getSchedules(ids) }
            .willReturn { Single.just(schedules) }
        given { reserveRepository.isAgreementAccepted() }
            .willReturn { Single.just(false) }

        instance.invoke()
            .assertSuccess()

        verify(dateProvider).getDate()
        verify(favoritesRepository).getActiveRecordReminderSchedules(date)
        verify(scheduleRepository).getSchedules(ids)
        verify(reserveRepository).isAgreementAccepted()
        schedules.forEach {
            verify(scheduleReminderNotifier).showNotificationToShowDetails(it)
        }
        verify(errorReporter, times(schedules.count()))
            .reportError(
                exception,
                "Ошибка при попытке формирования уведомления для записи на занятие"
            )
    }
}