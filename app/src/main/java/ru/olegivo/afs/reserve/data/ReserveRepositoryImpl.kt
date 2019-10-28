package ru.olegivo.afs.reserve.data

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import ru.olegivo.afs.preferences.data.PreferencesDataSource
import ru.olegivo.afs.reserve.domain.ReserveRepository
import ru.olegivo.afs.reserve.domain.models.Reserve
import ru.olegivo.afs.reserve.domain.models.ReserveContacts
import ru.olegivo.afs.schedule.data.ScheduleNetworkSource
import javax.inject.Inject

class ReserveRepositoryImpl @Inject constructor(
    private val reserveNetworkSource: ReserveNetworkSource,
    private val preferencesDataSource: PreferencesDataSource,
    private val scheduleNetworkSource: ScheduleNetworkSource
) : ReserveRepository {

    override fun saveReserveContacts(reserveContacts: ReserveContacts): Completable =
        preferencesDataSource.putString(Fio, reserveContacts.fio)
            .andThen(preferencesDataSource.putString(Phone, reserveContacts.phone))

    override fun getReserveContacts(): Maybe<ReserveContacts> =
        preferencesDataSource.getString(Fio).flatMap { fio ->
            preferencesDataSource.getString(Phone)
                .map { phone ->
                    ReserveContacts(fio, phone)
                }
        }

    override fun getAvailableSlots(clubId: Int, scheduleId: Long): Single<Int> =
        scheduleNetworkSource.getSlots(clubId, listOf(scheduleId))
            .map {
                it.singleOrNull()?.slots ?: 0
            }

    override fun reserve(reserve: Reserve) = reserveNetworkSource.reserve(reserve)

    companion object {
        internal const val Fio = "ReserveContacts.fio"
        internal const val Phone = "ReserveContacts.phone"
    }
}