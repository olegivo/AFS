package ru.olegivo.afs.clubs.data

import io.reactivex.Maybe
import io.reactivex.Scheduler
import ru.olegivo.afs.clubs.domain.ClubsRepository
import ru.olegivo.afs.preferences.data.PreferencesDataSource
import javax.inject.Inject
import javax.inject.Named

class ClubsRepositoryImpl @Inject constructor(
    private val clubsNetworkSource: ClubsNetworkSource,
    private val preferencesDataSource: PreferencesDataSource
) : ClubsRepository {
    override fun getClubs() = clubsNetworkSource.getClubs()

    override fun setCurrentClubId(clubId: Int) = preferencesDataSource.putInt(CURRENT_CLUB_ID, clubId)

    override fun getCurrentClubId() =
        preferencesDataSource.getInt(CURRENT_CLUB_ID, -1)
            .flatMapMaybe {
                if (it != -1) {
                    Maybe.just(it)
                } else {
                    Maybe.empty()
                }
            }

    companion object {
        private const val CURRENT_CLUB_ID = "CLUBS_CURRENT_CLUB_ID"
    }
}