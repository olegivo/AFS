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

import io.reactivex.Maybe
import io.reactivex.Scheduler
import ru.olegivo.afs.common.db.AfsDatabase
import ru.olegivo.afs.common.domain.DateProvider
import ru.olegivo.afs.extensions.toMaybe
import ru.olegivo.afs.favorites.domain.models.FavoriteFilter
import ru.olegivo.afs.favorites.domain.models.toFavoriteFilter
import ru.olegivo.afs.schedules.data.models.toDomain
import ru.olegivo.afs.schedules.db.models.toData
import javax.inject.Inject
import javax.inject.Named
import kotlin.math.abs

class GetClosestSportsActivityUseCaseImpl @Inject constructor(
    private val afsDatabase: AfsDatabase,
    private val dateProvider: DateProvider,
    @Named("io") private val ioScheduler: Scheduler
) : GetClosestSportsActivityUseCase {
    override fun invoke(favoriteFilter: FavoriteFilter, clubId: Int): Maybe<Long> {
        return afsDatabase.schedules.filterSchedules(favoriteFilter, clubId)
            .subscribeOn(ioScheduler)
            .flatMapMaybe { list ->
                val now = dateProvider.getDate()
                list.map { it.toData().toDomain() }
                    .filter { it.toFavoriteFilter() == favoriteFilter }
                    .minByOrNull { abs(it.datetime.time - now.time) }
                    ?.id
                    .toMaybe()
            }
    }
}
