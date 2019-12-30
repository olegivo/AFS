package ru.olegivo.afs.favorites.domain

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import ru.olegivo.afs.common.domain.DateProvider
import ru.olegivo.afs.schedule.domain.ReserveRepository
import ru.olegivo.afs.schedules.domain.ScheduleRepository
import ru.olegivo.afs.schedules.domain.models.Schedule
import timber.log.Timber
import javax.inject.Inject

class RestoreAllActiveRecordRemindersUseCaseImpl @Inject constructor(
    private val scheduleRepository: ScheduleRepository,
    private val favoritesRepository: FavoritesRepository,
    private val dateProvider: DateProvider,
    private val scheduleReminderNotifier: ScheduleReminderNotifier,
    private val reserveRepository: ReserveRepository
) : RestoreAllActiveRecordRemindersUseCase {

    override fun invoke(): Completable =
        favoritesRepository.getActiveRecordReminderSchedules(dateProvider.getDate())
            .flatMap {
                scheduleRepository.getSchedules(it)
            }
            .flatMapCompletable { schedules ->
                val actionSingle = reserveRepository.isAgreementAccepted()
                    .flatMapMaybe { isAgreementAccepted ->
                        val acceptedReserveContactsMaybe = if (!isAgreementAccepted) {
                            Maybe.empty()
                        } else {
                            reserveRepository.getReserveContacts()
                        }
                        acceptedReserveContactsMaybe
                            .map { reserveContacts ->
                                { s: Schedule ->
                                    scheduleReminderNotifier.showNotificationToReserve(
                                        s,
                                        reserveContacts.fio,
                                        reserveContacts.phone
                                    )
                                }
                            }
                    }
                    .switchIfEmpty(
                        Single.just(scheduleReminderNotifier::showNotificationToShowDetails)
                    )

                actionSingle.flatMapCompletable { action ->
                    Completable.concat(
                        schedules.map {
                            action(it)
                                .doOnError { throwable ->
                                    Timber.e(
                                        throwable,
                                        "Ошибка при попытке формирования уведомления для записи на занятие"
                                    )
                                }
                                .onErrorComplete()
                        }
                    )
                }
            }
}