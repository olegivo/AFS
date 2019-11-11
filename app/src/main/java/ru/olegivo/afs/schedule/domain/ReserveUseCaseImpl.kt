package ru.olegivo.afs.schedule.domain

import io.reactivex.Single
import ru.olegivo.afs.common.domain.DateProvider
import ru.olegivo.afs.extensions.andThen
import ru.olegivo.afs.schedule.domain.models.Reserve
import ru.olegivo.afs.schedule.domain.models.ReserveResult
import ru.olegivo.afs.schedules.domain.ScheduleRepository
import ru.olegivo.afs.schedules.domain.models.SportsActivity
import javax.inject.Inject

class ReserveUseCaseImpl @Inject constructor(
    private val dateProvider: DateProvider,
    private val reserveRepository: ReserveRepository,
    private val scheduleRepository: ScheduleRepository
) :
    ReserveUseCase {
    override fun reserve(
        sportsActivity: SportsActivity,
        fio: String,
        phone: String,
        hasAcceptedAgreement: Boolean
    ) =
        when {
            !hasAcceptedAgreement -> Single.just(ReserveResult.HaveToAcceptAgreement)
            sportsActivity.isReserved -> Single.just(ReserveResult.AlreadyReserved)
            sportsActivity.availableSlots ?: 0 == 0 -> Single.just(ReserveResult.NoSlots.APriori)
            dateProvider.getDate().after(sportsActivity.schedule.datetime) -> Single.just(
                ReserveResult.TheTimeHasGone
            )
            fio.isEmpty() || phone.isEmpty() -> Single.just(ReserveResult.NameAndPhoneShouldBeStated)
            else -> reserveRepository.getAvailableSlots(
                sportsActivity.schedule.clubId,
                sportsActivity.schedule.id
            )
                .flatMap { availableSlots ->
                    if (availableSlots == 0) {
                        Single.just(ReserveResult.NoSlots.APosteriori)
                    } else {
                        val reserve = Reserve(
                            fio,
                            phone,
                            sportsActivity.schedule.id,
                            sportsActivity.schedule.clubId
                        )
                        reserveRepository.reserve(reserve)
                            .andThen(Single.just(ReserveResult.Success))
                            .andThen { scheduleRepository.setScheduleReserved(sportsActivity.schedule) }
                    }
                }
        }
}