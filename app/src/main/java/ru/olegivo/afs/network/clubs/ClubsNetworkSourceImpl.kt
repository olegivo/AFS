package ru.olegivo.afs.network.clubs

import io.reactivex.Single
import ru.olegivo.afs.data.clubs.ClubsNetworkSource
import ru.olegivo.afs.domain.clubs.models.Club
import ru.olegivo.afs.network.Api
import javax.inject.Inject

class ClubsNetworkSourceImpl @Inject constructor(private val api: Api) : ClubsNetworkSource {
    override fun getClubs(): Single<List<Club>> {
        return api.getClubs()
    }
}