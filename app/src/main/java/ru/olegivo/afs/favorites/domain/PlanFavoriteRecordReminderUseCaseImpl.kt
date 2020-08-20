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
import ru.olegivo.afs.common.domain.DateProvider
import ru.olegivo.afs.extensions.toSingle
import ru.olegivo.afs.schedules.domain.models.Schedule
import javax.inject.Inject

class PlanFavoriteRecordReminderUseCaseImpl @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
    private val favoriteAlarmPlanner: FavoriteAlarmPlanner,
    private val dateProvider: DateProvider
) : PlanFavoriteRecordReminderUseCase {

    override fun invoke(schedule: Schedule): Completable =
        { dateProvider.getDate() }.toSingle()
            .flatMapCompletable { now ->
                val dateUntil = schedule.getReminderDateUntil()
                if (dateUntil > now) {
                    favoritesRepository.hasPlannedReminderToRecord(schedule)
                        .flatMapCompletable { hasPlannedReminderToRecord ->
                            if (hasPlannedReminderToRecord) {
                                Completable.complete()
                            } else {
                                val scheduleId = schedule.id
                                val dateFrom = schedule.getReminderDateFrom()
                                favoritesRepository.addReminderToRecord(
                                    schedueId = scheduleId,
                                    dateFrom = dateFrom,
                                    dateUntil = dateUntil
                                )
                                    .andThen(
                                        Completable.defer {
                                            favoriteAlarmPlanner.planFavoriteRecordReminder(schedule)
                                        }
                                    )
                            }
                        }
                } else {
                    Completable.complete()
                }
            }
}
