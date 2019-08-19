package ru.olegivo.afs.clubs.domain

import io.reactivex.Single
import ru.olegivo.afs.clubs.domain.models.Club

interface ClubsRepository {
    fun getClubs(): Single<List<Club>>
}
