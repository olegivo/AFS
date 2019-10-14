package ru.olegivo.afs.reserve.data

import io.reactivex.Single
import ru.olegivo.afs.reserve.domain.ReserveRepository
import ru.olegivo.afs.reserve.domain.models.Reserve
import javax.inject.Inject

class ReserveRepositoryImpl @Inject constructor(
    private val reserveNetworkSource: ReserveNetworkSource
) : ReserveRepository {
    override fun getAvailableSlots(clubId: Int, scheduleId: Long): Single<Int> =
        reserveNetworkSource.getSlots(clubId, listOf(scheduleId))
            .map {
                it.singleOrNull()?.slots ?: 0
            }

    override fun reserve(reserve: Reserve) = reserveNetworkSource.reserve(reserve)
}