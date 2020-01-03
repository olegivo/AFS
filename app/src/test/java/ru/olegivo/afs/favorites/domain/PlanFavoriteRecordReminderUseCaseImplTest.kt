package ru.olegivo.afs.favorites.domain

import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.willReturn
import io.reactivex.Completable
import org.junit.Test
import ru.olegivo.afs.BaseTestOf
import ru.olegivo.afs.common.add
import ru.olegivo.afs.common.domain.DateProvider
import ru.olegivo.afs.schedules.domain.models.createSchedule
import java.util.*

class PlanFavoriteRecordReminderUseCaseImplTest : BaseTestOf<PlanFavoriteRecordReminderUseCase>() {
    override fun createInstance() =
        PlanFavoriteRecordReminderUseCaseImpl(
            favoritesRepository,
            favoriteAlarmPlanner,
            dateProvider
        )

    private val favoritesRepository: FavoritesRepository = mock()
    private val favoriteAlarmPlanner: FavoriteAlarmPlanner = mock()
    private val dateProvider: DateProvider = mock()

    override fun getAllMocks() = arrayOf(
        favoritesRepository,
        favoriteAlarmPlanner,
        dateProvider
    )

    @Test
    fun `invoke PLANS reminder WHEN recordTo not passed`() {
        val recordTo = Date()
        val schedule = createSchedule().copy(recordTo = recordTo)
        val now = recordTo.add(hours = -1)

        given { dateProvider.getDate() }.willReturn { now }
        given { favoritesRepository.addReminderToRecord(schedule) }
            .willReturn { Completable.complete() }
        given { favoriteAlarmPlanner.planFavoriteRecordReminder(schedule) }
            .willReturn { Completable.complete() }

        instance.invoke(schedule)
            .assertSuccess()

        verify(dateProvider).getDate()
        verify(favoritesRepository).addReminderToRecord(schedule)
        verify(favoriteAlarmPlanner).planFavoriteRecordReminder(schedule)
    }

    @Test
    fun `invoke PLANS reminder WHEN recordTo not specified and start time not passed`() {
        val datetime = Date()
        val schedule = createSchedule().copy(recordTo = null, datetime = datetime)
        val now = datetime.add(hours = -1)

        given { dateProvider.getDate() }.willReturn { now }
        given { favoritesRepository.addReminderToRecord(schedule) }
            .willReturn { Completable.complete() }
        given { favoriteAlarmPlanner.planFavoriteRecordReminder(schedule) }
            .willReturn { Completable.complete() }

        instance.invoke(schedule)
            .assertSuccess()

        verify(dateProvider).getDate()
        verify(favoritesRepository).addReminderToRecord(schedule)
        verify(favoriteAlarmPlanner).planFavoriteRecordReminder(schedule)
    }

    @Test
    fun `invoke DOES nothing WHEN recordTo passed`() {
        val recordTo = Date()
        val schedule = createSchedule().copy(recordTo = recordTo)
        val now = recordTo.add(hours = 1)

        given { dateProvider.getDate() }.willReturn { now }

        instance.invoke(schedule)
            .assertSuccess()

        verify(dateProvider).getDate()
    }

    @Test
    fun `invoke DOES nothing WHEN recordTo not specified and start time passed`() {
        val datetime = Date()
        val schedule = createSchedule().copy(recordTo = null, datetime = datetime)
        val now = datetime.add(hours = 1)

        given { dateProvider.getDate() }.willReturn { now }

        instance.invoke(schedule)
            .assertSuccess()

        verify(dateProvider).getDate()
    }
}