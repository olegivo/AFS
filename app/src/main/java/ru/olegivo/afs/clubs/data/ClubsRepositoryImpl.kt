package ru.olegivo.afs.clubs.data

import io.reactivex.Scheduler
import ru.olegivo.afs.clubs.domain.ClubsRepository
import javax.inject.Inject

class ClubsRepositoryImpl @Inject constructor(
    private val clubsNetworkSource: ClubsNetworkSource,
    private val ioScheduler: Scheduler
) : ClubsRepository {
    override fun getClubs() = clubsNetworkSource.getClubs().subscribeOn(ioScheduler)
}