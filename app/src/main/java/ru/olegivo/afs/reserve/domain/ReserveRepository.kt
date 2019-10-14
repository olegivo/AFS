package ru.olegivo.afs.reserve.domain

import io.reactivex.Completable
import io.reactivex.Single
import ru.olegivo.afs.reserve.domain.models.Reserve

interface ReserveRepository {
    fun getAvailableSlots(clubId: Int, scheduleId: Long): Single<Int>
    fun reserve(reserve: Reserve): Completable
}
