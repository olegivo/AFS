package ru.olegivo.afs.schedule.domain

import io.reactivex.Completable
import io.reactivex.Single

interface SavedAgreementUseCase {
    fun isAgreementAccepted(): Single<Boolean>
    fun setAgreementAccepted(): Completable
}
