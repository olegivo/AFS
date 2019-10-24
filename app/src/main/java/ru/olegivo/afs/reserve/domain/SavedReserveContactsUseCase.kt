package ru.olegivo.afs.reserve.domain

import io.reactivex.Completable
import io.reactivex.Maybe
import ru.olegivo.afs.reserve.domain.models.ReserveContacts

interface SavedReserveContactsUseCase {
    fun saveReserveContacts(reserveContacts: ReserveContacts): Completable
    fun getReserveContacts(): Maybe<ReserveContacts>
}
