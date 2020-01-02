package ru.olegivo.afs.schedule.domain

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import ru.olegivo.afs.schedule.domain.models.Reserve
import ru.olegivo.afs.schedule.domain.models.ReserveContacts

interface ReserveRepository {
    fun getAvailableSlots(clubId: Int, scheduleId: Long): Single<Int>
    fun reserve(reserve: Reserve): Completable
    fun saveReserveContacts(reserveContacts: ReserveContacts): Completable
    fun getReserveContacts(): Maybe<ReserveContacts>
    fun isAgreementAccepted(): Single<Boolean>
    fun setAgreementAccepted(): Completable
    fun isStubReserve(): Single<Boolean>
    fun setStubReserve(isStubReserve: Boolean): Completable
}
