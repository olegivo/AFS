package ru.olegivo.afs.clubs.domain

import io.reactivex.Maybe
import javax.inject.Inject

class GetCurrentClubUseCaseImpl @Inject constructor(private val clubsRepository: ClubsRepository) :
    GetCurrentClubUseCase {
    override fun invoke(): Maybe<Int> = clubsRepository.getCurrentClubId()
}
