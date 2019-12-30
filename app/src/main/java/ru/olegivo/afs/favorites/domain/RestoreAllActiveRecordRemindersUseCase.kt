package ru.olegivo.afs.favorites.domain

import io.reactivex.Completable

interface RestoreAllActiveRecordRemindersUseCase {
    operator fun invoke(): Completable
}
