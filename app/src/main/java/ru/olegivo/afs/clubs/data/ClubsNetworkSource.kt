package ru.olegivo.afs.clubs.data

import io.reactivex.Single
import ru.olegivo.afs.clubs.domain.models.Club

interface ClubsNetworkSource {
    fun getClubs(): Single<List<Club>>
}
