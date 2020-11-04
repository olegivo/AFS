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
import ru.olegivo.afs.analytics.domain.AnalyticsProvider
import ru.olegivo.afs.favorites.analytics.FavoritesAnalytics
import ru.olegivo.afs.schedule.domain.ReserveRepository
import ru.olegivo.afs.schedules.domain.ScheduleRepository
import javax.inject.Inject

class ShowRecordReminderUseCaseImpl @Inject constructor(
    private val scheduleRepository: ScheduleRepository,
    private val scheduleReminderNotifier: ScheduleReminderNotifier,
    private val reserveRepository: ReserveRepository,
    private val analyticsProvider: AnalyticsProvider
) : ShowRecordReminderUseCase {

    override fun invoke(scheduleId: Long): Completable =
        analyticsProvider.logEvent(FavoritesAnalytics.ShowRecordReminder)
            .andThen(scheduleRepository.getSchedule(scheduleId))
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
                    .switchIfEmpty(
                        Single.defer {
                            scheduleReminderNotifier.showNotificationToShowDetails(schedule)
                                .andThen(Single.just(Unit))
                        }
                    )
                    .ignoreElement()
            }
}
