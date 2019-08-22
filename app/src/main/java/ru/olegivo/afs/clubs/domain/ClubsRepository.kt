package ru.olegivo.afs.clubs.domain

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import ru.olegivo.afs.clubs.domain.models.Club

interface ClubsRepository {
    fun getClubs(): Single<List<Club>>
    fun setCurrentClubId(clubId: Int): Completable
    fun getCurrentClubId(): Maybe<Int>
}
