package ru.olegivo.afs.domain.clubs

import io.reactivex.Single
import ru.olegivo.afs.domain.clubs.models.Club
import javax.inject.Inject

class GetClubsUseCaseImpl @Inject constructor(private val clubsRepository: ClubsRepository) : GetClubsUseCase {
    override fun invoke(): Single<List<Club>> {
        return clubsRepository.getClubs()
    }
}