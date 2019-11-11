package ru.olegivo.afs.schedule.data

import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import ru.olegivo.afs.BaseTestOf
import ru.olegivo.afs.helpers.getRandomBoolean
import ru.olegivo.afs.helpers.getRandomInt
import ru.olegivo.afs.helpers.getRandomLong
import ru.olegivo.afs.helpers.getRandomString
import ru.olegivo.afs.preferences.data.PreferencesDataSource
import ru.olegivo.afs.schedule.domain.ReserveRepository
import ru.olegivo.afs.schedule.domain.models.Reserve
import ru.olegivo.afs.schedules.data.ScheduleNetworkSource
import ru.olegivo.afs.schedules.domain.models.Slot
import ru.olegivo.afs.schedules.domain.models.createReserveContacts

class ReserveRepositoryImplTest : BaseTestOf<ReserveRepository>() {
    override fun createInstance(): ReserveRepository = ReserveRepositoryImpl(
        reserveNetworkSource,
        preferencesDataSource,
        scheduleNetworkSource
    )

    //<editor-fold desc="mocks">
    private val reserveNetworkSource: ReserveNetworkSource = mock()
    private val preferencesDataSource: PreferencesDataSource = mock()
    private val scheduleNetworkSource: ScheduleNetworkSource = mock()

    override fun getAllMocks() = arrayOf(
        reserveNetworkSource,
        preferencesDataSource,
        scheduleNetworkSource
    )
    //</editor-fold>

    @Test
    fun `getAvailableSlots RETURNS value from network source`() {
        val clubId = getRandomInt()
        val scheduleId = getRandomLong()
        val expected = getRandomInt()
        given(scheduleNetworkSource.getSlots(clubId, listOf(scheduleId)))
            .willReturn(Single.just(listOf(
                Slot(
                    getRandomLong(),
                    expected
                )
            )))

        val availableSlots = instance.getAvailableSlots(clubId, scheduleId)
            .test().andTriggerActions()
            .assertNoErrors()
            .values().single()

        assertThat(availableSlots).isEqualTo(expected)
        verify(scheduleNetworkSource).getSlots(clubId, listOf(scheduleId))
    }

    @Test
    fun `getAvailableSlots RETURNS 0 WHEN returned slot is null`() {
        val clubId = getRandomInt()
        val scheduleId = getRandomLong()
        given(scheduleNetworkSource.getSlots(clubId, listOf(scheduleId)))
            .willReturn(Single.just(listOf(
                Slot(
                    getRandomLong(),
                    null
                )
            )))

        val availableSlots = instance.getAvailableSlots(clubId, scheduleId)
            .test().andTriggerActions()
            .assertNoErrors()
            .values().single()

        assertThat(availableSlots).isEqualTo(0)
        verify(scheduleNetworkSource).getSlots(clubId, listOf(scheduleId))
    }

    @Test
    fun `getAvailableSlots RETURNS 0 WHEN returned slots is empty`() {
        val clubId = getRandomInt()
        val scheduleId = getRandomLong()
        given(scheduleNetworkSource.getSlots(clubId, listOf(scheduleId)))
            .willReturn(Single.just(emptyList()))

        val availableSlots = instance.getAvailableSlots(clubId, scheduleId)
            .test().andTriggerActions()
            .assertNoErrors()
            .values().single()

        assertThat(availableSlots).isEqualTo(0)
        verify(scheduleNetworkSource).getSlots(clubId, listOf(scheduleId))
    }

    @Test
    fun `reserve COMPLETES successfully`() {
        val clubId = getRandomInt()
        val scheduleId = getRandomLong()
        val fio = getRandomString()
        val phone = getRandomString()

        val reserve = Reserve(fio, phone, scheduleId, clubId)
        given(reserveNetworkSource.reserve(reserve))
            .willReturn(Completable.complete())

        instance.reserve(reserve)
            .test().andTriggerActions()
            .assertNoErrors()
            .assertComplete()

        verify(reserveNetworkSource).reserve(reserve)
    }

    @Test
    fun `saveReserveContacts COMPLETES successfully`() {
        val reserveContacts = createReserveContacts()
        given(preferencesDataSource.putString(ReserveRepositoryImpl.Fio, reserveContacts.fio))
            .willReturn(Completable.complete())
        given(preferencesDataSource.putString(ReserveRepositoryImpl.Phone, reserveContacts.phone))
            .willReturn(Completable.complete())

        instance.saveReserveContacts(reserveContacts)
            .test().andTriggerActions()
            .assertNoErrors()
            .assertComplete()

        verify(preferencesDataSource).putString(ReserveRepositoryImpl.Fio, reserveContacts.fio)
        verify(preferencesDataSource).putString(ReserveRepositoryImpl.Phone, reserveContacts.phone)
    }

    @Test
    fun `getReserveContacts RETURNS data from prefs`() {
        val createReserveContacts = createReserveContacts()
        val fio = createReserveContacts.fio
        val phone = createReserveContacts.phone
        given(preferencesDataSource.getString(ReserveRepositoryImpl.Fio))
            .willReturn(Maybe.just(fio))
        given(preferencesDataSource.getString(ReserveRepositoryImpl.Phone))
            .willReturn(Maybe.just(phone))

        val reserveContacts = instance.getReserveContacts()
            .test().andTriggerActions()
            .assertNoErrors()
            .values().single()

        assertThat(reserveContacts).isEqualTo(createReserveContacts)

        verify(preferencesDataSource).getString(ReserveRepositoryImpl.Fio)
        verify(preferencesDataSource).getString(ReserveRepositoryImpl.Phone)
    }

    @Test
    fun `isAgreementAccepted RETURNS value from prefs`() {
        val expected = getRandomBoolean()
        given(preferencesDataSource.getBoolean(ReserveRepositoryImpl.IsAgreementAccepted))
            .willReturn(Maybe.just(expected))

        val isAgreementAccepted = instance.isAgreementAccepted()
            .test().andTriggerActions()
            .assertNoErrors()
            .assertComplete()
            .values().single()

        assertThat(isAgreementAccepted).isEqualTo(expected)
        verify(preferencesDataSource).getBoolean(ReserveRepositoryImpl.IsAgreementAccepted)
    }

    @Test
    fun `setAgreementAccepted`() {
        given(preferencesDataSource.putBoolean(ReserveRepositoryImpl.IsAgreementAccepted, true))
            .willReturn(Completable.complete())

        instance.setAgreementAccepted()
            .test().andTriggerActions()
            .assertNoErrors()
            .assertComplete()

        verify(preferencesDataSource).putBoolean(ReserveRepositoryImpl.IsAgreementAccepted, true)
    }
}