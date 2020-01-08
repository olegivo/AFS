package ru.olegivo.afs.favorites.domain

import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.willReturn
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import org.junit.Test
import ru.olegivo.afs.BaseTestOf
import ru.olegivo.afs.helpers.getRandomLong
import ru.olegivo.afs.schedule.domain.ReserveRepository
import ru.olegivo.afs.schedules.domain.ScheduleRepository
import ru.olegivo.afs.schedules.domain.models.createReserveContacts
import ru.olegivo.afs.schedules.domain.models.createSchedule

class ShowRecordReminderUseCaseImplTest : BaseTestOf<ShowRecordReminderUseCase>() {

    override fun createInstance() =
        ShowRecordReminderUseCaseImpl(
            scheduleRepository,
            scheduleReminderNotifier,
            reserveRepository
        )

    //<editor-fold desc="mocks">
    private val scheduleRepository: ScheduleRepository = mock()
    private val scheduleReminderNotifier: ScheduleReminderNotifier = mock()
    private val reserveRepository: ReserveRepository = mock()

    override fun getAllMocks() =
        arrayOf(
            scheduleRepository,
            reserveRepository,
            scheduleReminderNotifier
        )
    //</editor-fold>

    @Test
    fun `invoke SHOWS record notification to show details WHEN agreement not accepted`() {
        val scheduleId = getRandomLong()
        val schedule = createSchedule()
        given { scheduleRepository.getSchedule(scheduleId) }
            .willReturn { Single.just(schedule) }
        given { scheduleReminderNotifier.showNotificationToShowDetails(schedule) }
            .willReturn { Completable.complete() }
        given { reserveRepository.isAgreementAccepted() }
            .willReturn { Single.just(false) }

        instance.invoke(scheduleId)
            .assertSuccess()

        verify(scheduleRepository).getSchedule(scheduleId)
        verify(reserveRepository).isAgreementAccepted()
        verify(scheduleReminderNotifier).showNotificationToShowDetails(schedule)
    }

    @Test
    fun `invoke SHOWS record notification to show details WHEN agreement accepted, has no reserve contacts`() {
        val scheduleId = getRandomLong()
        val schedule = createSchedule()
        given { scheduleRepository.getSchedule(scheduleId) }
            .willReturn { Single.just(schedule) }
        given { scheduleReminderNotifier.showNotificationToShowDetails(schedule) }
            .willReturn { Completable.complete() }
        given { reserveRepository.isAgreementAccepted() }
            .willReturn { Single.just(true) }
        given { reserveRepository.getReserveContacts() }
            .willReturn { Maybe.empty() }

        instance.invoke(scheduleId)
            .assertSuccess()

        verify(scheduleRepository).getSchedule(scheduleId)
        verify(reserveRepository).isAgreementAccepted()
        verify(reserveRepository).getReserveContacts()
        verify(scheduleReminderNotifier).showNotificationToShowDetails(schedule)
    }

    @Test
    fun `invoke SHOWS record notification to reserve WHEN agreement accepted, has reserve contacts`() {
        val scheduleId = getRandomLong()
        val schedule = createSchedule()
        val reserveContacts = createReserveContacts()

        given { scheduleRepository.getSchedule(scheduleId) }
            .willReturn { Single.just(schedule) }
        given {
            scheduleReminderNotifier.showNotificationToReserve(
                schedule,
                reserveContacts.fio,
                reserveContacts.phone
            )
        }.willReturn { Completable.complete() }
        given { reserveRepository.isAgreementAccepted() }
            .willReturn { Single.just(true) }
        given { reserveRepository.getReserveContacts() }
            .willReturn { Maybe.just(reserveContacts) }

        instance.invoke(scheduleId)
            .assertSuccess()

        verify(scheduleRepository).getSchedule(scheduleId)
        verify(reserveRepository).isAgreementAccepted()
        verify(reserveRepository).getReserveContacts()
        verify(scheduleReminderNotifier).showNotificationToReserve(
            schedule,
            reserveContacts.fio,
            reserveContacts.phone
        )
    }

}