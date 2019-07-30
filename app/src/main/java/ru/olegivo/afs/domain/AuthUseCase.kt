package ru.olegivo.afs.domain

import io.reactivex.Completable

interface AuthUseCase {
    operator fun invoke(): Completable
}