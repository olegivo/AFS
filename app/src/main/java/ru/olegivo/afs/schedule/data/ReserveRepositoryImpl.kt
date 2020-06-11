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

package ru.olegivo.afs.schedule.data

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import ru.olegivo.afs.preferences.data.PreferencesDataSource
import ru.olegivo.afs.schedule.domain.ReserveRepository
import ru.olegivo.afs.schedule.domain.models.Reserve
import ru.olegivo.afs.schedule.domain.models.ReserveContacts
import ru.olegivo.afs.schedules.data.ScheduleNetworkSource
import javax.inject.Inject

class ReserveRepositoryImpl @Inject constructor(
    private val reserveNetworkSource: ReserveNetworkSource,
    private val preferencesDataSource: PreferencesDataSource,
    private val scheduleNetworkSource: ScheduleNetworkSource
) : ReserveRepository {

    override fun saveReserveContacts(reserveContacts: ReserveContacts): Completable =
        preferencesDataSource.putString(Fio, reserveContacts.fio)
            .andThen(preferencesDataSource.putString(Phone, reserveContacts.phone))

    override fun getReserveContacts(): Maybe<ReserveContacts> =
        preferencesDataSource.getString(Fio).flatMap { fio ->
            preferencesDataSource.getString(Phone)
                .map { phone ->
                    ReserveContacts(fio, phone)
                }
        }

    override fun getAvailableSlots(clubId: Int, scheduleId: Long): Single<Int> =
        scheduleNetworkSource.getSlots(clubId, listOf(scheduleId))
            .map {
                it.singleOrNull()?.slots ?: 0
            }

    override fun reserve(reserve: Reserve) = reserveNetworkSource.reserve(reserve)

    override fun isAgreementAccepted(): Single<Boolean> =
        preferencesDataSource.getBoolean(IsAgreementAccepted)
            .switchIfEmpty(Single.just(false))

    override fun setAgreementAccepted(): Completable {
        return preferencesDataSource.putBoolean(IsAgreementAccepted, true)
    }

    override fun isStubReserve(): Single<Boolean> =
        preferencesDataSource.getBoolean(IsStubReserve)
            .switchIfEmpty(Single.just(false))

    override fun setStubReserve(isStubReserve: Boolean): Completable {
        return preferencesDataSource.putBoolean(IsStubReserve, isStubReserve)
    }

    companion object {
        internal const val Fio = "ReserveContacts.fio"
        internal const val Phone = "ReserveContacts.phone"
        internal const val IsAgreementAccepted = "PersonalDataAgreement.isAccepted"
        internal const val IsStubReserve = "Reservation.isStubReserve"
    }
}
