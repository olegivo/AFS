package ru.olegivo.afs.reserve.network

import io.reactivex.Scheduler
import io.reactivex.Single
import ru.olegivo.afs.common.network.Api
import ru.olegivo.afs.common.network.NetworkErrorsMapper
import ru.olegivo.afs.common.network.mapCompletableError
import ru.olegivo.afs.reserve.data.ReserveNetworkSource
import ru.olegivo.afs.reserve.domain.models.Reserve
import ru.olegivo.afs.reserve.network.models.ReserveRequestError
import ru.olegivo.afs.reserve.network.models.toNetwork
import ru.olegivo.afs.schedule.data.models.Slot
import javax.inject.Inject
import javax.inject.Named

class ReserveNetworkSourceImpl @Inject constructor(
    private val api: Api,
    private val networkErrorsMapper: NetworkErrorsMapper,
    @Named("io") private val ioScheduler: Scheduler,
    @Named("computation") private val computationScheduler: Scheduler
) : ReserveNetworkSource {

    override fun getSlots(clubId: Int, ids: List<Long>): Single<List<Slot>> {
        val idByPosition =
            ids.mapIndexed { index, id -> index.toString() to id.toString() }
                .associate { it }
        return api.getSlots(clubId, idByPosition)
            .subscribeOn(ioScheduler)
            .observeOn(computationScheduler)
            .map { slots ->
                slots.map { Slot(it.id, it.slots) }
            }
    }

    override fun reserve(reserve: Reserve) =
        api.reserve(reserve.toNetwork())
            .mapCompletableError<ReserveRequestError>(networkErrorsMapper)
}