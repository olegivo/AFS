package ru.olegivo.afs.data.clubs

import io.reactivex.Single
import ru.olegivo.afs.domain.clubs.models.Club

interface ClubsNetworkSource {
    fun getClubs(): Single<List<Club>>
}
