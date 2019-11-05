package ru.olegivo.afs.schedule.data

import io.reactivex.Completable
import ru.olegivo.afs.schedule.domain.models.Reserve

interface ReserveNetworkSource {
    fun reserve(reserve: Reserve): Completable
}
