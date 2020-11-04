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

package ru.olegivo.afs.schedules.domain

import io.reactivex.Completable
import io.reactivex.Scheduler
import ru.olegivo.afs.analytics.domain.AnalyticsProvider
import ru.olegivo.afs.extensions.andThen
import ru.olegivo.afs.favorites.domain.FavoritesRepository
import ru.olegivo.afs.favorites.domain.models.filterByFavorites
import ru.olegivo.afs.favorites.domain.PlanFavoriteRecordReminderUseCase
import ru.olegivo.afs.schedules.analytics.SchedulesAnalytic
import javax.inject.Inject
import javax.inject.Named

class ActualizeScheduleUseCaseImpl @Inject constructor(
    private val scheduleRepository: ScheduleRepository,
    private val favoritesRepository: FavoritesRepository,
    private val planFavoriteRecordReminder: PlanFavoriteRecordReminderUseCase,
    @Named("computation") private val computationScheduler: Scheduler,
    private val analyticsProvider: AnalyticsProvider
) :
    ActualizeScheduleUseCase {

    override fun invoke(clubId: Int): Completable =
        scheduleRepository.actualizeSchedules(clubId)
            .andThen {
                analyticsProvider.logEvent(SchedulesAnalytic.ActualizeSchedules)
            }
            .flatMapCompletable { schedules ->
                favoritesRepository.getFavoriteFilters()
                    .observeOn(computationScheduler)
                    .map { favoriteFilters -> schedules.filterByFavorites(favoriteFilters) }
                    .flatMapCompletable { favoriteSchedules ->
                        Completable.concat(
                            favoriteSchedules.map {
                                planFavoriteRecordReminder(it)
                            }
                        )
                    }
            }
}
