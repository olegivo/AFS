package ru.olegivo.afs.schedules.domain

import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.Single
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import ru.olegivo.afs.BaseTestOf
import ru.olegivo.afs.favorites.domain.FavoritesRepository
import ru.olegivo.afs.favorites.domain.models.toFavoriteFilter
import ru.olegivo.afs.randomSubList
import ru.olegivo.afs.repeat
import ru.olegivo.afs.schedules.domain.models.createSchedule
import ru.olegivo.afs.schedules.domain.models.createSlot
import kotlin.random.Random

class GetCurrentWeekSportsActivitiesUseCaseImplTest : BaseTestOf<GetCurrentWeekScheduleUseCase>() {

    override fun createInstance() = GetCurrentWeekSportsActivitiesUseCaseImpl(
        scheduleRepository,
        favoritesRepository,
        schedulerRule.testScheduler
    )

    //<editor-fold desc="mocks">
    private val scheduleRepository: ScheduleRepository = mock()
    private val favoritesRepository: FavoritesRepository = mock()

    override fun getAllMocks() = arrayOf(
        scheduleRepository,
        favoritesRepository
    )
    //</editor-fold>

    @Test
    fun `invoke returns data from repository`() {
        val schedules = { createSchedule() }.repeat(20)
        val clubId = Random.nextInt()
        given(scheduleRepository.getCurrentWeekSchedule(clubId)).willReturn(Single.just(schedules))
        val ids = schedules.map { it.id }
        val slots = ids.map(::createSlot)
        given(scheduleRepository.getSlots(clubId, ids)).willReturn(Single.just(slots))
        val reservedScheduleIds = ids.randomSubList()
        given(scheduleRepository.getCurrentWeekReservedScheduleIds())
            .willReturn(Single.just(reservedScheduleIds))
        val favoriteSchedules = schedules.randomSubList()
        val favoriteScheduleIds = favoriteSchedules.map { it.id }
        val favoriteFilters = favoriteSchedules.map { schedule ->
            schedule.toFavoriteFilter()
        }
        given(favoritesRepository.getFavoriteFilters())
            .willReturn(Single.just(favoriteFilters))

        instance.invoke(clubId)
            .assertResult { sportsActivities ->
                assertThat(sportsActivities.map { it.schedule }).isEqualTo(schedules)
                assertThat(sportsActivities.filter { it.isReserved }.map { it.schedule.id })
                    .isEqualTo(reservedScheduleIds)
                assertThat(sportsActivities.filter { it.isFavorite }.map { it.schedule.id })
                    .isEqualTo(favoriteScheduleIds)
            }

        verify(scheduleRepository).getCurrentWeekSchedule(clubId)
        verify(scheduleRepository).getSlots(clubId, ids)
        verify(scheduleRepository).getCurrentWeekReservedScheduleIds()
        verify(favoritesRepository).getFavoriteFilters()
    }

}