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

package ru.olegivo.afs.favorites.data

import io.reactivex.Completable
import io.reactivex.Single
import ru.olegivo.afs.favorites.domain.FavoritesRepository
import ru.olegivo.afs.favorites.domain.models.FavoriteFilter
import ru.olegivo.afs.favorites.domain.models.toFavoriteFilter
import ru.olegivo.afs.schedules.domain.models.Schedule
import java.util.Date
import javax.inject.Inject

class FavoritesRepositoryImpl @Inject constructor(private val favoritesDbSource: FavoritesDbSource) :
    FavoritesRepository {

    override fun getFavoriteFilters(): Single<List<FavoriteFilter>> =
        favoritesDbSource.getFavoriteFilters()

    override fun addFilter(favoriteFilter: FavoriteFilter): Completable =
        favoritesDbSource.addFilter(favoriteFilter)

    override fun removeFilter(favoriteFilter: FavoriteFilter): Completable =
        favoritesDbSource.removeFilter(favoriteFilter)

    override fun addReminderToRecord(
        schedueId: Long,
        dateFrom: Date,
        dateUntil: Date
    ): Completable =
        favoritesDbSource.addReminderToRecord(
            scheduleId = schedueId,
            dateFrom = dateFrom,
            dateUntil = dateUntil
        )

    override fun hasPlannedReminderToRecord(schedule: Schedule): Single<Boolean> =
        favoritesDbSource.hasPlannedReminderToRecord(schedule)

    override fun getActiveRecordReminderSchedules(moment: Date): Single<List<Long>> =
        favoritesDbSource.getActiveRecordReminderSchedules(moment)

    override fun isFavorite(schedule: Schedule): Single<Boolean> =
        favoritesDbSource.exist(schedule.toFavoriteFilter())
}
