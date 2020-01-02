package ru.olegivo.afs.schedule.domain

import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.willReturn
import io.reactivex.Completable
import io.reactivex.Single
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import ru.olegivo.afs.BaseTestOf
import ru.olegivo.afs.common.add
import ru.olegivo.afs.common.domain.DateProvider
import ru.olegivo.afs.helpers.getRandomString
import ru.olegivo.afs.schedule.domain.models.Reserve
import ru.olegivo.afs.schedule.domain.models.ReserveResult
import ru.olegivo.afs.schedules.domain.ScheduleRepository
import ru.olegivo.afs.schedules.domain.models.createSportsActivity
import java.util.*

class ReserveUseCaseImplTest : BaseTestOf<ReserveUseCase>() {

    override fun createInstance() = ReserveUseCaseImpl(
        dateProvider,
        reserveRepository,
        scheduleRepository
    )

    //<editor-fold desc="mocks">
    private val dateProvider: DateProvider = mock()
    private val reserveRepository: ReserveRepository = mock()
    private val scheduleRepository: ScheduleRepository = mock()

    override fun getAllMocks() = arrayOf(
        dateProvider,
        reserveRepository,
        scheduleRepository
    )
    //</editor-fold>

    @Test
    fun `reserve returns ReserveResult-NoSlots-APriori WHEN has no slots a priori, the time hasn't gone, isReserved = false`() {
        val now = Date()
        given(dateProvider.getDate()).willReturn { now }
        val sportsActivity = createSportsActivity(
            totalSlots = 21,
            availableSlots = 0,
            datetime = now.add(minutes = 1),
            isReserved = false
        )
        val fio = getRandomString()
        val phone = getRandomString()

        val result = instance.reserve(sportsActivity, fio, phone, true)
            .test().andTriggerActions()
            .assertNoErrors()
            .values().single()

        assertThat(result).isEqualTo(ReserveResult.NoSlots.APriori)
    }

    @Test
    fun `reserve returns ReserveResult-HaveToAcceptAgreement WHEN not accepted agreement`() {
        val now = Date()
        val sportsActivity = createSportsActivity(
            totalSlots = 21,
            availableSlots = 1,
            datetime = now.add(minutes = 1),
            isReserved = false
        )
        val fio = getRandomString()
        val phone = getRandomString()

        val result = instance.reserve(sportsActivity, fio, phone, hasAcceptedAgreement = false)
            .test().andTriggerActions()
            .assertNoErrors()
            .values().single()

        assertThat(result).isEqualTo(ReserveResult.HaveToAcceptAgreement)
    }

    @Test
    fun `reserve returns ReserveResult-NoSlots-APosteriori WHEN has no slots a posteriori (other concurrent reserve on server decremented available slots), the time hasn't gone, isReserved = false`() {
        val now = Date()
        given(dateProvider.getDate()).willReturn { now }
        val sportsActivity = createSportsActivity(
            totalSlots = 21,
            availableSlots = 1,
            datetime = now.add(minutes = 1),
            isReserved = false
        )
        given(
            reserveRepository.getAvailableSlots(
                sportsActivity.schedule.clubId,
                sportsActivity.schedule.id
            )
        )
            .willReturn { Single.just(0) }
        val fio = getRandomString()
        val phone = getRandomString()

        val result = instance.reserve(sportsActivity, fio, phone, true)
            .test().andTriggerActions()
            .assertNoErrors()
            .values().single()

        assertThat(result).isEqualTo(ReserveResult.NoSlots.APosteriori)

        verify(dateProvider).getDate()
        verify(reserveRepository).getAvailableSlots(
            sportsActivity.schedule.clubId,
            sportsActivity.schedule.id
        )
    }

    @Test
    fun `reserve returns ReserveResult-TheTimeHasGone WHEN the time has gone, has available slots, isReserved = false`() {
        val now = Date()
        given(dateProvider.getDate()).willReturn { now }
        val sportsActivity = createSportsActivity(
            totalSlots = 21,
            availableSlots = 1,
            datetime = now.add(minutes = -1),
            isReserved = false
        )
        val fio = getRandomString()
        val phone = getRandomString()

        val result = instance.reserve(sportsActivity, fio, phone, true)
            .test().andTriggerActions()
            .assertNoErrors()
            .values().single()

        assertThat(result).isEqualTo(ReserveResult.TheTimeHasGone)

        verify(dateProvider).getDate()
    }

    @Test
    fun `reserve returns ReserveResult-NameAndPhoneShouldBeStated WHEN the time hasn't gone, has available slots, fio is empty, phone is not empty, isReserved = false`() {
        val now = Date()
        given(dateProvider.getDate()).willReturn { now }
        val sportsActivity = createSportsActivity(
            totalSlots = 21,
            availableSlots = 1,
            datetime = now.add(minutes = 1),
            isReserved = false
        )
        val fio = ""
        val phone = getRandomString()
        given(
            reserveRepository.getAvailableSlots(
                sportsActivity.schedule.clubId,
                sportsActivity.schedule.id
            )
        )
            .willReturn { Single.just(1) }
        val reserve =
            Reserve(fio, phone, sportsActivity.schedule.id, sportsActivity.schedule.clubId)
        given(reserveRepository.reserve(reserve))
            .willReturn { Completable.complete() }

        val result = instance.reserve(sportsActivity, fio, phone, true)
            .test().andTriggerActions()
            .assertNoErrors()
            .values().single()

        assertThat(result).isEqualTo(ReserveResult.NameAndPhoneShouldBeStated)

        verify(dateProvider).getDate()
    }

    @Test
    fun `reserve returns ReserveResult-NameAndPhoneShouldBeStated WHEN the time hasn't gone, has available slots, fio is not empty, phone is empty, isReserved = false`() {
        val now = Date()
        given(dateProvider.getDate()).willReturn { now }
        val sportsActivity = createSportsActivity(
            totalSlots = 21,
            availableSlots = 1,
            datetime = now.add(minutes = 1),
            isReserved = false
        )
        val fio = getRandomString()
        val phone = ""
        given(
            reserveRepository.getAvailableSlots(
                sportsActivity.schedule.clubId,
                sportsActivity.schedule.id
            )
        )
            .willReturn { Single.just(1) }
        val reserve =
            Reserve(fio, phone, sportsActivity.schedule.id, sportsActivity.schedule.clubId)
        given(reserveRepository.reserve(reserve))
            .willReturn { Completable.complete() }

        val result = instance.reserve(sportsActivity, fio, phone, true)
            .test().andTriggerActions()
            .assertNoErrors()
            .values().single()

        assertThat(result).isEqualTo(ReserveResult.NameAndPhoneShouldBeStated)

        verify(dateProvider).getDate()
    }

    @Test
    fun `reserve returns ReserveResult-AlreadyReserved WHEN the time hasn't gone, has available slots, fio is not empty, phone is not empty, isReserved = true`() {
        val now = Date()
        val sportsActivity = createSportsActivity(
            totalSlots = 21,
            availableSlots = 1,
            datetime = now.add(minutes = 1),
            isReserved = true
        )
        val fio = getRandomString()
        val phone = getRandomString()
        given(
            reserveRepository.getAvailableSlots(
                sportsActivity.schedule.clubId,
                sportsActivity.schedule.id
            )
        )
            .willReturn { Single.just(1) }
        val reserve =
            Reserve(fio, phone, sportsActivity.schedule.id, sportsActivity.schedule.clubId)
        given(reserveRepository.reserve(reserve))
            .willReturn { Completable.complete() }

        val result = instance.reserve(sportsActivity, fio, phone, true)
            .test().andTriggerActions()
            .assertNoErrors()
            .values().single()

        assertThat(result).isEqualTo(ReserveResult.AlreadyReserved)
    }

    @Test
    fun `reserve returns ReserveResult-Success WHEN the time hasn't gone, has available slots, isReserved = false`() {
        val now = Date()
        given(dateProvider.getDate()).willReturn { now }
        val sportsActivity = createSportsActivity(
            totalSlots = 21,
            availableSlots = 1,
            datetime = now.add(minutes = 1),
            isReserved = false
        )
        val fio = getRandomString()
        val phone = getRandomString()
        given(
            reserveRepository.getAvailableSlots(
                sportsActivity.schedule.clubId,
                sportsActivity.schedule.id
            )
        )
            .willReturn { Single.just(1) }
        val reserve =
            Reserve(fio, phone, sportsActivity.schedule.id, sportsActivity.schedule.clubId)
        given(reserveRepository.reserve(reserve))
            .willReturn { Completable.complete() }
        given(scheduleRepository.setScheduleReserved(sportsActivity.schedule)).willReturn { Completable.complete() }

        val result = instance.reserve(sportsActivity, fio, phone, true)
            .test().andTriggerActions()
            .assertNoErrors()
            .values().single()

        assertThat(result).isEqualTo(ReserveResult.Success)

        verify(dateProvider).getDate()
        verify(reserveRepository).getAvailableSlots(
            sportsActivity.schedule.clubId,
            sportsActivity.schedule.id
        )
        verify(reserveRepository).reserve(reserve)
        verify(scheduleRepository).setScheduleReserved(sportsActivity.schedule)
    }
}