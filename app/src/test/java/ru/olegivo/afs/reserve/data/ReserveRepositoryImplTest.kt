package ru.olegivo.afs.reserve.data

import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.Completable
import io.reactivex.Single
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import ru.olegivo.afs.BaseTestOf
import ru.olegivo.afs.helpers.getRandomInt
import ru.olegivo.afs.helpers.getRandomLong
import ru.olegivo.afs.helpers.getRandomString
import ru.olegivo.afs.reserve.domain.ReserveRepository
import ru.olegivo.afs.reserve.domain.models.Reserve
import ru.olegivo.afs.schedule.data.models.Slot

class ReserveRepositoryImplTest : BaseTestOf<ReserveRepository>() {
    override fun createInstance(): ReserveRepository = ReserveRepositoryImpl(reserveNetworkSource)

    //<editor-fold desc="mocks">
    private val reserveNetworkSource: ReserveNetworkSource = mock()

    override fun getAllMocks() = arrayOf<Any>(reserveNetworkSource)
    //</editor-fold>

    @Test
    fun `getAvailableSlots RETURNS value from network source`() {
        val clubId = getRandomInt()
        val scheduleId = getRandomLong()
        val expected = getRandomInt()
        given(reserveNetworkSource.getSlots(clubId, listOf(scheduleId)))
            .willReturn(Single.just(listOf(Slot(getRandomLong(), expected))))

        val availableSlots = instance.getAvailableSlots(clubId, scheduleId)
            .test().andTriggerActions()
            .assertNoErrors()
            .values().single()

        assertThat(availableSlots).isEqualTo(expected)
        verify(reserveNetworkSource).getSlots(clubId, listOf(scheduleId))
    }

    @Test
    fun `getAvailableSlots RETURNS 0 WHEN returned slot is null`() {
        val clubId = getRandomInt()
        val scheduleId = getRandomLong()
        given(reserveNetworkSource.getSlots(clubId, listOf(scheduleId)))
            .willReturn(Single.just(listOf(Slot(getRandomLong(), null))))

        val availableSlots = instance.getAvailableSlots(clubId, scheduleId)
            .test().andTriggerActions()
            .assertNoErrors()
            .values().single()

        assertThat(availableSlots).isEqualTo(0)
        verify(reserveNetworkSource).getSlots(clubId, listOf(scheduleId))
    }

    @Test
    fun `getAvailableSlots RETURNS 0 WHEN returned slots is empty`() {
        val clubId = getRandomInt()
        val scheduleId = getRandomLong()
        given(reserveNetworkSource.getSlots(clubId, listOf(scheduleId)))
            .willReturn(Single.just(emptyList()))

        val availableSlots = instance.getAvailableSlots(clubId, scheduleId)
            .test().andTriggerActions()
            .assertNoErrors()
            .values().single()

        assertThat(availableSlots).isEqualTo(0)
        verify(reserveNetworkSource).getSlots(clubId, listOf(scheduleId))
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
}