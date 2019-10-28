package ru.olegivo.afs.reserve.network

import ru.olegivo.afs.common.network.Api
import ru.olegivo.afs.common.network.NetworkErrorsMapper
import ru.olegivo.afs.common.network.mapCompletableError
import ru.olegivo.afs.reserve.data.ReserveNetworkSource
import ru.olegivo.afs.reserve.domain.models.Reserve
import ru.olegivo.afs.reserve.network.models.ReserveRequestError
import ru.olegivo.afs.reserve.network.models.toNetwork
import javax.inject.Inject

class ReserveNetworkSourceImpl @Inject constructor(
    private val api: Api,
    private val networkErrorsMapper: NetworkErrorsMapper
) : ReserveNetworkSource {

    override fun reserve(reserve: Reserve) =
        api.reserve(reserve.toNetwork())
            .mapCompletableError<ReserveRequestError>(networkErrorsMapper)
}