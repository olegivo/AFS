package ru.olegivo.afs.schedule.domain

import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.Single
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import ru.olegivo.afs.BaseTestOf
import ru.olegivo.afs.favorites.domain.FavoritesRepository
import ru.olegivo.afs.helpers.getRandomBoolean
import ru.olegivo.afs.helpers.getRandomInt
import ru.olegivo.afs.helpers.getRandomLong
import ru.olegivo.afs.schedules.domain.ScheduleRepository
import ru.olegivo.afs.schedules.domain.models.createSchedule
import ru.olegivo.afs.schedules.domain.models.createSlot

class GetSportsActivityUseCaseImplTest : BaseTestOf<GetSportsActivityUseCase>() {

    override fun createInstance() = GetSportsActivityUseCaseImpl(
        scheduleRepository,
        favoritesRepository
    )

    //<editor-fold desc="mocks">
    private val scheduleRepository: ScheduleRepository = mock()
    private val favoritesRepository: FavoritesRepository = mock()

    override fun getAllMocks(): Array<Any> = arrayOf(
        scheduleRepository,
        favoritesRepository
    )
    //</editor-fold>

    @Test
    fun `invoke RETURNS sports activity from repository`() {
        val clubId = getRandomInt()
        val scheduleId = getRandomLong()
        val schedule = createSchedule()
        val slot = createSlot(scheduleId)
        val isReserved = getRandomBoolean()
        val isFavorite = getRandomBoolean()

        given(scheduleRepository.getSchedule(scheduleId))
            .willReturn(Single.just(schedule))
        given(scheduleRepository.getSlots(clubId, listOf(scheduleId)))
            .willReturn(Single.just(listOf(slot)))
        given(scheduleRepository.isScheduleReserved(scheduleId))
            .willReturn(Single.just(isReserved))
        given(favoritesRepository.isFavorite(schedule))
            .willReturn(Single.just(isFavorite))

        val result = instance.invoke(clubId, scheduleId)
            .test().andTriggerActions()
            .assertNoErrors()
            .values().single()

        verify(scheduleRepository).getSchedule(scheduleId)
        verify(scheduleRepository).getSlots(clubId, listOf(scheduleId))
        verify(scheduleRepository).isScheduleReserved(scheduleId)
        verify(favoritesRepository).isFavorite(schedule)

        assertThat(result.schedule).isEqualTo(schedule)
        assertThat(result.availableSlots).isEqualTo(slot.slots!!)
        assertThat(result.isReserved).isEqualTo(isReserved)
        assertThat(result.isFavorite).isEqualTo(isFavorite)
    }
}