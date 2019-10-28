package ru.olegivo.afs.reserve.domain

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import ru.olegivo.afs.reserve.domain.models.Reserve
import ru.olegivo.afs.reserve.domain.models.ReserveContacts
import ru.olegivo.afs.schedule.domain.models.Schedule

interface ReserveRepository {
    fun getAvailableSlots(clubId: Int, scheduleId: Long): Single<Int>
    fun reserve(reserve: Reserve): Completable
    fun saveReserveContacts(reserveContacts: ReserveContacts): Completable
    fun getReserveContacts(): Maybe<ReserveContacts>
}
