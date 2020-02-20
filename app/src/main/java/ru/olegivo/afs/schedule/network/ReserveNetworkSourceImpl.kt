package ru.olegivo.afs.schedule.network

import io.reactivex.Scheduler
import ru.olegivo.afs.common.network.Api
import ru.olegivo.afs.common.network.NetworkErrorsMapper
import ru.olegivo.afs.common.network.mapCompletableError
import ru.olegivo.afs.schedule.data.ReserveNetworkSource
import ru.olegivo.afs.schedule.domain.models.Reserve
import ru.olegivo.afs.schedule.network.models.ReserveRequestError
import ru.olegivo.afs.schedule.network.models.toNetwork
import javax.inject.Inject
import javax.inject.Named

class ReserveNetworkSourceImpl @Inject constructor(
    private val api: Api,
    private val networkErrorsMapper: NetworkErrorsMapper,
    @Named("io") private val ioScheduler: Scheduler
) : ReserveNetworkSource {

    override fun reserve(reserve: Reserve) =
        api.reserve(reserve.toNetwork())
            .subscribeOn(ioScheduler)
            .mapCompletableError<ReserveRequestError>(networkErrorsMapper)
}
