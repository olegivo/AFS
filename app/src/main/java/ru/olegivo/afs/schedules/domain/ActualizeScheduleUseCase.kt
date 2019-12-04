package ru.olegivo.afs.schedules.domain

import io.reactivex.Completable

interface ActualizeScheduleUseCase {
    operator fun invoke(clubId: Int): Completable
}