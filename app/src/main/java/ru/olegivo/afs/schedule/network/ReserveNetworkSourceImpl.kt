/*
 * Copyright (C) 2020 Oleg Ivashchenko <olegivo@gmail.com>
 *
 * This file is part of AFS.
 *
 * AFS is free software: you can redistribute it and/or modify
 * it under the terms of the MIT License.
 *
 * AFS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * AFS.
 */

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
