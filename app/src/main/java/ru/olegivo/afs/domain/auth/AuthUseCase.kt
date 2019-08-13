package ru.olegivo.afs.domain.auth

import io.reactivex.Completable

interface AuthUseCase {
    operator fun invoke(): Completable
}