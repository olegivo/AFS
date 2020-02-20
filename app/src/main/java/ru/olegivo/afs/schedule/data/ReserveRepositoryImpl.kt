package ru.olegivo.afs.schedule.data

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import ru.olegivo.afs.preferences.data.PreferencesDataSource
import ru.olegivo.afs.schedule.domain.ReserveRepository
import ru.olegivo.afs.schedule.domain.models.Reserve
import ru.olegivo.afs.schedule.domain.models.ReserveContacts
import ru.olegivo.afs.schedules.data.ScheduleNetworkSource
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

    override fun isAgreementAccepted(): Single<Boolean> =
        preferencesDataSource.getBoolean(IsAgreementAccepted)
            .switchIfEmpty(Single.just(false))

    override fun setAgreementAccepted(): Completable {
        return preferencesDataSource.putBoolean(IsAgreementAccepted, true)
    }

    override fun isStubReserve(): Single<Boolean> =
        preferencesDataSource.getBoolean(IsStubReserve)
            .switchIfEmpty(Single.just(false))

    override fun setStubReserve(isStubReserve: Boolean): Completable {
        return preferencesDataSource.putBoolean(IsStubReserve, isStubReserve)
    }

    companion object {
        internal const val Fio = "ReserveContacts.fio"
        internal const val Phone = "ReserveContacts.phone"
        internal const val IsAgreementAccepted = "PersonalDataAgreement.isAccepted"
        internal const val IsStubReserve = "Reservation.isStubReserve"
    }
}
