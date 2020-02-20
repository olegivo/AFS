package ru.olegivo.afs.clubs.network

import io.reactivex.Scheduler
import io.reactivex.Single
import ru.olegivo.afs.clubs.data.ClubsNetworkSource
import ru.olegivo.afs.clubs.domain.models.Club
import ru.olegivo.afs.common.network.Api
import javax.inject.Inject
import javax.inject.Named

class ClubsNetworkSourceImpl @Inject constructor(
    private val api: Api,
    @Named("io") private val ioScheduler: Scheduler
) : ClubsNetworkSource {
    override fun getClubs(): Single<List<Club>> {
        return api.getClubs().subscribeOn(ioScheduler)
    }
}
