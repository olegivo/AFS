package ru.olegivo.afs.data.clubs

import io.reactivex.Scheduler
import ru.olegivo.afs.domain.clubs.ClubsRepository
import javax.inject.Inject

class ClubsRepositoryImpl @Inject constructor(
    private val clubsNetworkSource: ClubsNetworkSource,
    private val ioScheduler: Scheduler
) : ClubsRepository {
    override fun getClubs() = clubsNetworkSource.getClubs().subscribeOn(ioScheduler)
}