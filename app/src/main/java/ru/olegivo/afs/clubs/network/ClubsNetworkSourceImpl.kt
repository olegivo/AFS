package ru.olegivo.afs.clubs.network

import io.reactivex.Single
import ru.olegivo.afs.clubs.data.ClubsNetworkSource
import ru.olegivo.afs.clubs.domain.models.Club
import ru.olegivo.afs.common.network.Api
import javax.inject.Inject

class ClubsNetworkSourceImpl @Inject constructor(private val api: Api) : ClubsNetworkSource {
    override fun getClubs(): Single<List<Club>> {
        return api.getClubs()
    }
}