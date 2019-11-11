package ru.olegivo.afs.schedule.domain

import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class SavedAgreementUseCaseImpl @Inject constructor(private val reserveRepository: ReserveRepository): SavedAgreementUseCase {
    override fun isAgreementAccepted(): Single<Boolean> =
        reserveRepository.isAgreementAccepted()

    override fun setAgreementAccepted(): Completable =
        reserveRepository.setAgreementAccepted()
}