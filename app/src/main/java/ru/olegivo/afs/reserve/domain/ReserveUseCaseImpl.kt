package ru.olegivo.afs.reserve.domain

import io.reactivex.Single
import ru.olegivo.afs.common.domain.DateProvider
import ru.olegivo.afs.common.domain.HttpCallFailureException
import ru.olegivo.afs.reserve.domain.models.Reserve
import ru.olegivo.afs.reserve.domain.models.ReserveResult
import ru.olegivo.afs.schedule.domain.models.Schedule
import javax.inject.Inject

class ReserveUseCaseImpl @Inject constructor(
    private val dateProvider: DateProvider,
    private val reserveRepository: ReserveRepository
) :
    ReserveUseCase {
    override fun reserve(schedule: Schedule, fio: String, phone: String): Single<ReserveResult> {
        return when {
            schedule.availableSlots ?: 0 == 0 -> Single.just(ReserveResult.NoSlots.APriori)
            dateProvider.getDate().after(schedule.datetime) -> Single.just(ReserveResult.TheTimeHasGone)
            fio.isEmpty() || phone.isEmpty() -> Single.just(ReserveResult.NameAndPhoneShouldBeStated)
            else -> reserveRepository.getAvailableSlots(schedule.clubId, schedule.id)
                .flatMap { availableSlots ->
                    if (availableSlots == 0) {
                        Single.just(ReserveResult.NoSlots.APosteriori)
                    } else {
                        val reserve = Reserve(fio, phone, schedule.id, schedule.clubId)
                        reserveRepository.reserve(reserve)
                            .andThen(Single.just(ReserveResult.Success))
                    }
                }
        }
    }
}