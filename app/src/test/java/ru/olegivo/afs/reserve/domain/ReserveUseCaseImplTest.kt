package ru.olegivo.afs.reserve.domain

import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.Completable
import io.reactivex.Single
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import ru.olegivo.afs.BaseTestOf
import ru.olegivo.afs.common.add
import ru.olegivo.afs.common.domain.DateProvider
import ru.olegivo.afs.helpers.getRandomString
import ru.olegivo.afs.reserve.domain.models.Reserve
import ru.olegivo.afs.reserve.domain.models.ReserveResult
import ru.olegivo.afs.schedule.domain.models.createSchedule
import java.util.*

class ReserveUseCaseImplTest : BaseTestOf<ReserveUseCase>() {
    override fun createInstance() = ReserveUseCaseImpl(dateProvider, reserveRepository)

    //<editor-fold desc="mocks">
    private val dateProvider: DateProvider = mock()
    private val reserveRepository: ReserveRepository = mock()

    override fun getAllMocks() = arrayOf(
        dateProvider,
        reserveRepository
    )
    //</editor-fold>

    @Test
    fun `reserve returns ReserveResult-NoSlots-APriori WHEN has no slots a priori, the time hasn't gone`() {
        val now = Date()
        given(dateProvider.getDate()).willReturn(now)
        val schedule = createSchedule().copy(
            totalSlots = 21,
            availableSlots = 0,
            datetime = now.add(minutes = 1)
        )
        val fio = getRandomString()
        val phone = getRandomString()

        val result = instance.reserve(schedule, fio, phone)
            .test().andTriggerActions()
            .assertNoErrors()
            .values().single()

        assertThat(result).isEqualTo(ReserveResult.NoSlots.APriori)
    }

    @Test
    fun `reserve returns ReserveResult-NoSlots-APosteriori WHEN has no slots a posteriori (other concurrent reserve on server decremented available slots), the time hasn't gone`() {
        val now = Date()
        given(dateProvider.getDate()).willReturn(now)
        val schedule = createSchedule().copy(
            totalSlots = 21,
            availableSlots = 1,
            datetime = now.add(minutes = 1)
        )
        given(reserveRepository.getAvailableSlots(schedule.clubId, schedule.id))
            .willReturn(Single.just(0))
        val fio = getRandomString()
        val phone = getRandomString()

        val result = instance.reserve(schedule, fio, phone)
            .test().andTriggerActions()
            .assertNoErrors()
            .values().single()

        assertThat(result).isEqualTo(ReserveResult.NoSlots.APosteriori)

        verify(dateProvider).getDate()
        verify(reserveRepository).getAvailableSlots(schedule.clubId, schedule.id)
    }

    @Test
    fun `reserve returns ReserveResult-TheTimeHasGone WHEN the time has gone, has available slots`() {
        val now = Date()
        given(dateProvider.getDate()).willReturn(now)
        val schedule = createSchedule().copy(
            totalSlots = 21,
            availableSlots = 1,
            datetime = now.add(minutes = -1)
        )
        val fio = getRandomString()
        val phone = getRandomString()

        val result = instance.reserve(schedule, fio, phone)
            .test().andTriggerActions()
            .assertNoErrors()
            .values().single()

        assertThat(result).isEqualTo(ReserveResult.TheTimeHasGone)

        verify(dateProvider).getDate()
    }

    @Test
    fun `reserve returns ReserveResult-NameAndPhoneShouldBeStated WHEN the time hasn't gone, has available slots, fio is empty, phone is not empty`() {
        val now = Date()
        given(dateProvider.getDate()).willReturn(now)
        val schedule = createSchedule().copy(
            totalSlots = 21,
            availableSlots = 1,
            datetime = now.add(minutes = 1)
        )
        val fio = ""
        val phone = getRandomString()
        given(reserveRepository.getAvailableSlots(schedule.clubId, schedule.id))
            .willReturn(Single.just(1))
        val reserve = Reserve(fio, phone, schedule.id, schedule.clubId)
        given(reserveRepository.reserve(reserve)).willReturn(
            Completable.complete()
        )

        val result = instance.reserve(schedule, fio, phone)
            .test().andTriggerActions()
            .assertNoErrors()
            .values().single()

        assertThat(result).isEqualTo(ReserveResult.NameAndPhoneShouldBeStated)

        verify(dateProvider).getDate()
    }

    @Test
    fun `reserve returns ReserveResult-NameAndPhoneShouldBeStated WHEN the time hasn't gone, has available slots, fio is not empty, phone is empty`() {
        val now = Date()
        given(dateProvider.getDate()).willReturn(now)
        val schedule = createSchedule().copy(
            totalSlots = 21,
            availableSlots = 1,
            datetime = now.add(minutes = 1)
        )
        val fio = getRandomString()
        val phone = ""
        given(reserveRepository.getAvailableSlots(schedule.clubId, schedule.id))
            .willReturn(Single.just(1))
        val reserve = Reserve(fio, phone, schedule.id, schedule.clubId)
        given(reserveRepository.reserve(reserve)).willReturn(
            Completable.complete()
        )

        val result = instance.reserve(schedule, fio, phone)
            .test().andTriggerActions()
            .assertNoErrors()
            .values().single()

        assertThat(result).isEqualTo(ReserveResult.NameAndPhoneShouldBeStated)

        verify(dateProvider).getDate()
    }

    @Test
    fun `reserve returns ReserveResult-Success WHEN the time hasn't gone, has available slots`() {
        val now = Date()
        given(dateProvider.getDate()).willReturn(now)
        val schedule = createSchedule().copy(
            totalSlots = 21,
            availableSlots = 1,
            datetime = now.add(minutes = 1)
        )
        val fio = getRandomString()
        val phone = getRandomString()
        given(reserveRepository.getAvailableSlots(schedule.clubId, schedule.id))
            .willReturn(Single.just(1))
        val reserve = Reserve(fio, phone, schedule.id, schedule.clubId)
        given(reserveRepository.reserve(reserve)).willReturn(
            Completable.complete()
        )

        val result = instance.reserve(schedule, fio, phone)
            .test().andTriggerActions()
            .assertNoErrors()
            .values().single()

        assertThat(result).isEqualTo(ReserveResult.Success)

        verify(dateProvider).getDate()
        verify(reserveRepository).getAvailableSlots(schedule.clubId, schedule.id)
        verify(reserveRepository).reserve(reserve)
    }
}