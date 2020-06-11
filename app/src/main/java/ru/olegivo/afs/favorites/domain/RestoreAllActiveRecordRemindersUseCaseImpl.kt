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

package ru.olegivo.afs.favorites.domain

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import ru.olegivo.afs.common.domain.DateProvider
import ru.olegivo.afs.common.domain.ErrorReporter
import ru.olegivo.afs.schedule.domain.ReserveRepository
import ru.olegivo.afs.schedules.domain.ScheduleRepository
import ru.olegivo.afs.schedules.domain.models.Schedule
import javax.inject.Inject

class RestoreAllActiveRecordRemindersUseCaseImpl @Inject constructor(
    private val scheduleRepository: ScheduleRepository,
    private val favoritesRepository: FavoritesRepository,
    private val dateProvider: DateProvider,
    private val scheduleReminderNotifier: ScheduleReminderNotifier,
    private val reserveRepository: ReserveRepository,
    private val errorReporter: ErrorReporter
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
                                    errorReporter.reportError(
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
