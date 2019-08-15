package ru.olegivo.afs.domain.clubs

import io.reactivex.Single
import ru.olegivo.afs.domain.clubs.models.Club

interface ClubsRepository {
    fun getClubs(): Single<List<Club>>
}
