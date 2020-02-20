package ru.olegivo.afs.clubs.domain

import io.reactivex.Completable

interface SetCurrentClubUseCase {
    operator fun invoke(clubId: Int): Completable
}
