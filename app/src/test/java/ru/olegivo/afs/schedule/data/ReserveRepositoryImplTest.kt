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

package ru.olegivo.afs.schedule.data

import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyBlocking
import io.reactivex.Completable
import io.reactivex.Maybe
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import ru.olegivo.afs.BaseTestOf
import ru.olegivo.afs.helpers.getRandomBoolean
import ru.olegivo.afs.helpers.getRandomInt
import ru.olegivo.afs.helpers.getRandomLong
import ru.olegivo.afs.helpers.getRandomString
import ru.olegivo.afs.helpers.givenBlocking
import ru.olegivo.afs.helpers.willReturn
import ru.olegivo.afs.preferences.data.PreferencesDataSource
import ru.olegivo.afs.schedule.domain.ReserveRepository
import ru.olegivo.afs.schedule.domain.models.Reserve
import ru.olegivo.afs.schedules.data.ScheduleNetworkSource
import ru.olegivo.afs.schedules.domain.models.Slot
import ru.olegivo.afs.schedules.domain.models.createReserveContacts

class ReserveRepositoryImplTest : BaseTestOf<ReserveRepository>() {
    override fun createInstance(): ReserveRepository = ReserveRepositoryImpl(
        reserveNetworkSource = reserveNetworkSource,
        preferencesDataSource = preferencesDataSource,
        scheduleNetworkSource = scheduleNetworkSource,
        coroutineToRxAdapter = coroutineToRxAdapter
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
        val ids = listOf(scheduleId)
        givenBlocking(scheduleNetworkSource) { getSlots(clubId, ids) }
            .willReturn { listOf(Slot(getRandomLong(), expected)) }

        val availableSlots = instance.getAvailableSlots(clubId, scheduleId)
            .test().andTriggerActions()
            .assertNoErrors()
            .values().single()

        assertThat(availableSlots).isEqualTo(expected)
        verifyBlocking(scheduleNetworkSource) { getSlots(clubId, ids) }
    }

    @Test
    fun `getAvailableSlots RETURNS 0 WHEN returned slot is null`() {
        val clubId = getRandomInt()
        val scheduleId = getRandomLong()
        val ids = listOf(scheduleId)
        givenBlocking(scheduleNetworkSource) { getSlots(clubId, ids) }
            .willReturn { listOf(Slot(getRandomLong(), null)) }

        val availableSlots = instance.getAvailableSlots(clubId, scheduleId)
            .test().andTriggerActions()
            .assertNoErrors()
            .values().single()

        assertThat(availableSlots).isEqualTo(0)
        verifyBlocking(scheduleNetworkSource) { getSlots(clubId, ids) }
    }

    @Test
    fun `getAvailableSlots RETURNS 0 WHEN returned slots is empty`() {
        val clubId = getRandomInt()
        val scheduleId = getRandomLong()
        val ids = listOf(scheduleId)
        givenBlocking(scheduleNetworkSource) { getSlots(clubId, ids) }
            .willReturn { emptyList() }

        val availableSlots = instance.getAvailableSlots(clubId, scheduleId)
            .test().andTriggerActions()
            .assertNoErrors()
            .values().single()

        assertThat(availableSlots).isEqualTo(0)
        verifyBlocking(scheduleNetworkSource) { getSlots(clubId, ids) }
    }

    @Test
    fun `reserve COMPLETES successfully`() {
        val clubId = getRandomInt()
        val scheduleId = getRandomLong()
        val fio = getRandomString()
        val phone = getRandomString()

        val reserve = Reserve(fio, phone, scheduleId, clubId)

        instance.reserve(reserve)
            .test().andTriggerActions()
            .assertNoErrors()
            .assertComplete()

        verifyBlocking(reserveNetworkSource) { reserve(reserve) }
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
