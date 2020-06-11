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
import ru.olegivo.afs.favorites.domain.models.FavoriteFilter
import ru.olegivo.afs.schedules.domain.models.Schedule
import java.util.*

interface FavoritesDbSource {
    fun addFilter(favoriteFilter: FavoriteFilter): Completable
    fun getFavoriteFilters(): Single<List<FavoriteFilter>>
    fun removeFilter(favoriteFilter: FavoriteFilter): Completable
    fun exist(favoriteFilter: FavoriteFilter): Single<Boolean>
    fun getActiveRecordReminderSchedules(moment: Date): Single<List<Long>>
    fun addReminderToRecord(schedule: Schedule): Completable
    fun hasPlannedReminderToRecord(schedule: Schedule): Single<Boolean>
}
