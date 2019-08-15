package ru.olegivo.afs.domain.clubs

import io.reactivex.Single
import ru.olegivo.afs.domain.clubs.models.Club

interface GetClubsUseCase {
    operator fun invoke(): Single<List<Club>>
}