package ru.olegivo.afs.clubs.domain

import io.reactivex.Completable
import javax.inject.Inject

class SetCurrentClubUseCaseImpl @Inject constructor(private val clubsRepository: ClubsRepository) : SetCurrentClubUseCase {
    override fun invoke(clubId: Int): Completable = clubsRepository.setCurrentClubId(clubId)
}
