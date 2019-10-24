package ru.olegivo.afs.reserve.domain

import io.reactivex.Maybe
import ru.olegivo.afs.reserve.domain.models.ReserveContacts
import javax.inject.Inject

class SavedReserveContactsUseCaseImpl @Inject constructor(private val reserveRepository: ReserveRepository) :
    SavedReserveContactsUseCase {

    override fun saveReserveContacts(reserveContacts: ReserveContacts) =
        reserveRepository.saveReserveContacts(reserveContacts)

    override fun getReserveContacts(): Maybe<ReserveContacts> =
        reserveRepository.getReserveContacts()
}