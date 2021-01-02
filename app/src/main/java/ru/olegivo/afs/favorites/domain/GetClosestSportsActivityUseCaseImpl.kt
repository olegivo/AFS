/*
 * Copyright (C) 2021 Oleg Ivashchenko <olegivo@gmail.com>
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
import ru.olegivo.afs.common.domain.DateProvider
import ru.olegivo.afs.extensions.toMaybe
import ru.olegivo.afs.favorites.domain.models.FavoriteFilter
import ru.olegivo.afs.favorites.domain.models.toFavoriteFilter
import ru.olegivo.afs.schedules.domain.ScheduleRepository
import javax.inject.Inject
import javax.inject.Named
import kotlin.math.abs

class GetClosestSportsActivityUseCaseImpl @Inject constructor(
    private val dateProvider: DateProvider,
    private val scheduleRepository: ScheduleRepository,
    @Named("io") private val ioScheduler: Scheduler
) : GetClosestSportsActivityUseCase {
    override fun invoke(favoriteFilter: FavoriteFilter, clubId: Int): Maybe<Long> {
        return scheduleRepository.filterSchedules(favoriteFilter, clubId)
            .flatMapMaybe { list ->
                val now = dateProvider.getDate()
                list
                    .filter { it.toFavoriteFilter() == favoriteFilter }
                    .minByOrNull { abs(it.datetime.time - now.time) }
                    ?.id
                    .toMaybe()
            }
    }
}
