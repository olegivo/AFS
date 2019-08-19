package ru.olegivo.afs.clubs.domain

import io.reactivex.Single
import ru.olegivo.afs.clubs.domain.models.Club

interface GetClubsUseCase {
    operator fun invoke(): Single<List<Club>>
}