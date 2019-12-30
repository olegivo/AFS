package ru.olegivo.afs.favorites.domain

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import ru.olegivo.afs.schedule.domain.ReserveRepository
import ru.olegivo.afs.schedules.domain.ScheduleRepository
import javax.inject.Inject

class ShowRecordReminderUseCaseImpl @Inject constructor(
    private val scheduleRepository: ScheduleRepository,
    private val scheduleReminderNotifier: ScheduleReminderNotifier,
    private val reserveRepository: ReserveRepository
) : ShowRecordReminderUseCase {

    override fun invoke(scheduleId: Long): Completable =
        scheduleRepository.getSchedule(scheduleId)
            .flatMapCompletable { schedule ->
                reserveRepository.isAgreementAccepted()
                    .flatMapMaybe { isAgreementAccepted ->
                        val acceptedReserveContactsMaybe = if (!isAgreementAccepted) {
                            Maybe.empty()
                        } else {
                            reserveRepository.getReserveContacts()
                        }
                        acceptedReserveContactsMaybe
                            .flatMap { reserveContacts ->
                                scheduleReminderNotifier.showNotificationToReserve(
                                    schedule,
                                    reserveContacts.fio,
                                    reserveContacts.phone
                                ).andThen(Maybe.just(Unit))
                            }
                    }
                    .switchIfEmpty(Single.defer {
                        scheduleReminderNotifier.showNotificationToShowDetails(schedule)
                            .andThen(Single.just(Unit))
                    })
                    .ignoreElement()
            }
}