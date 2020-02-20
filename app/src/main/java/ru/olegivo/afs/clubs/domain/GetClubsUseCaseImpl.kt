package ru.olegivo.afs.clubs.domain

import io.reactivex.Single
import ru.olegivo.afs.clubs.domain.models.Club
import javax.inject.Inject

class GetClubsUseCaseImpl @Inject constructor(private val clubsRepository: ClubsRepository) :
    GetClubsUseCase {
    override fun invoke(): Single<List<Club>> {
        return clubsRepository.getClubs()
    }
}
