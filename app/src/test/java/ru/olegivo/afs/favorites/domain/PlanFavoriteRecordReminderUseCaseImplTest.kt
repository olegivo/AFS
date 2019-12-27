package ru.olegivo.afs.favorites.domain

import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.willReturn
import io.reactivex.Completable
import org.junit.Test
import ru.olegivo.afs.BaseTestOf
import ru.olegivo.afs.schedules.domain.models.createSchedule

class PlanFavoriteRecordReminderUseCaseImplTest : BaseTestOf<PlanFavoriteRecordReminderUseCase>() {
    override fun createInstance() =
        PlanFavoriteRecordReminderUseCaseImpl(favoritesRepository, favoriteAlarmPlanner)

    private val favoritesRepository: FavoritesRepository = mock()
    private val favoriteAlarmPlanner: FavoriteAlarmPlanner = mock()

    override fun getAllMocks() = arrayOf(
        favoritesRepository,
        favoriteAlarmPlanner
    )

    @Test
    fun `invoke `() {
        val schedule = createSchedule()

        given { favoritesRepository.addReminderToRecord(schedule) }
            .willReturn { Completable.complete() }
        given { favoriteAlarmPlanner.planFavoriteRecordReminder(schedule) }
            .willReturn { Completable.complete() }

        instance.invoke(schedule)
            .assertSuccess()
        
        verify(favoritesRepository).addReminderToRecord(schedule)
        verify(favoriteAlarmPlanner).planFavoriteRecordReminder(schedule)
    }
}