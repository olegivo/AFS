package ru.olegivo.afs.auth.domain

import io.reactivex.Completable

interface AuthUseCase {
    operator fun invoke(): Completable
}
