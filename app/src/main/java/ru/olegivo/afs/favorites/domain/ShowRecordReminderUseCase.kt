package ru.olegivo.afs.favorites.domain

import io.reactivex.Completable

interface ShowRecordReminderUseCase {
    operator fun invoke(scheduleId: Long): Completable
}
