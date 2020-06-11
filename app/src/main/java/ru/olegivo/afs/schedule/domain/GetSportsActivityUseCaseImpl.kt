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

package ru.olegivo.afs.schedule.domain

import io.reactivex.Single
import ru.olegivo.afs.favorites.domain.FavoritesRepository
import ru.olegivo.afs.schedules.domain.ScheduleRepository
import ru.olegivo.afs.schedules.domain.models.SportsActivity
import javax.inject.Inject

class GetSportsActivityUseCaseImpl @Inject constructor(
    private val scheduleRepository: ScheduleRepository,
    private val favoritesRepository: FavoritesRepository
) :
    GetSportsActivityUseCase {
    override fun invoke(clubId: Int, scheduleId: Long): Single<SportsActivity> {
        return scheduleRepository.getSchedule(scheduleId)
            .flatMap { schedule ->
                scheduleRepository.getSlots(clubId, listOf(scheduleId))
                    .flatMap { slots ->
                        val availableSlots: Int? =
                            slots.singleOrNull { it.id == scheduleId }
                                ?.slots
                                ?: 0
                        scheduleRepository.isScheduleReserved(scheduleId)
                            .flatMap { isReserved ->
                                favoritesRepository.isFavorite(schedule)
                                    .map { isFavorite ->
                                        SportsActivity(
                                            schedule = schedule,
                                            availableSlots = availableSlots,
                                            isReserved = isReserved,
                                            isFavorite = isFavorite
                                        )
                                    }
                            }
                    }
            }
    }
}
