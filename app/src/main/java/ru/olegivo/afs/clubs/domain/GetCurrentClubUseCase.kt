package ru.olegivo.afs.clubs.domain

import io.reactivex.Maybe

interface GetCurrentClubUseCase {
    operator fun invoke(): Maybe<Int>
}