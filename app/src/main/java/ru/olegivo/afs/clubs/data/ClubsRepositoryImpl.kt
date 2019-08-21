package ru.olegivo.afs.clubs.data

import io.reactivex.Scheduler
import ru.olegivo.afs.clubs.domain.ClubsRepository
import javax.inject.Inject
import javax.inject.Named

class ClubsRepositoryImpl @Inject constructor(
    private val clubsNetworkSource: ClubsNetworkSource,
    private val preferencesDataSource: PreferencesDataSource
) : ClubsRepository {
    override fun getClubs() = clubsNetworkSource.getClubs()
}