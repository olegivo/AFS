package ru.olegivo.afs.reserve.data

import io.reactivex.Completable
import io.reactivex.Single
import ru.olegivo.afs.reserve.domain.models.Reserve
import ru.olegivo.afs.schedule.data.models.Slot

interface ReserveNetworkSource {
    fun reserve(reserve: Reserve): Completable
}
