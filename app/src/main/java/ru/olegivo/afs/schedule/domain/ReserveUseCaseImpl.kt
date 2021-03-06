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

import io.reactivex.Completable
import io.reactivex.Single
import ru.olegivo.afs.common.domain.DateProvider
import ru.olegivo.afs.extensions.andThen
import ru.olegivo.afs.schedule.domain.models.Reserve
import ru.olegivo.afs.schedule.domain.models.ReserveResult
import ru.olegivo.afs.schedules.domain.ScheduleRepository
import ru.olegivo.afs.schedules.domain.models.SportsActivity
import javax.inject.Inject

class ReserveUseCaseImpl @Inject constructor(
    private val dateProvider: DateProvider,
    private val reserveRepository: ReserveRepository,
    private val scheduleRepository: ScheduleRepository
) :
    ReserveUseCase {
    override fun reserve(
        sportsActivity: SportsActivity,
        fio: String,
        phone: String,
        hasAcceptedAgreement: Boolean
    ) =
        when {
            !hasAcceptedAgreement -> Single.just(ReserveResult.HaveToAcceptAgreement)
            sportsActivity.isReserved -> Single.just(ReserveResult.AlreadyReserved)
            sportsActivity.availableSlots ?: 0 == 0 -> Single.just(ReserveResult.NoSlots.APriori)
            dateProvider.getDate().after(sportsActivity.schedule.datetime) -> Single.just(
                ReserveResult.TheTimeHasGone
            )
            fio.isEmpty() || phone.isEmpty() -> Single.just(ReserveResult.NameAndPhoneShouldBeStated)
            else ->
                reserveRepository.getAvailableSlots(
                    sportsActivity.schedule.clubId,
                    sportsActivity.schedule.id
                )
                    .flatMap { availableSlots ->
                        if (availableSlots == 0) {
                            Single.just(ReserveResult.NoSlots.APosteriori)
                        } else {
                            reserveRepository.isStubReserve()
                                .flatMapCompletable { isStubReserve ->
                                    if (isStubReserve) {
                                        Completable.complete()
                                    } else {
                                        val reserve = Reserve(
                                            fio,
                                            phone,
                                            sportsActivity.schedule.id,
                                            sportsActivity.schedule.clubId
                                        )
                                        reserveRepository.reserve(reserve)
                                    }
                                }
                                .andThen(Single.just(ReserveResult.Success))
                                .andThen { scheduleRepository.setScheduleReserved(sportsActivity.schedule) }
                        }
                    }
        }
}
