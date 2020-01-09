package ru.olegivo.afs.favorites.domain

import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.Completable
import org.junit.Test
import ru.olegivo.afs.BaseTestOf
import ru.olegivo.afs.common.getDateWithoutTime
import ru.olegivo.afs.favorites.domain.models.FavoriteFilter
import ru.olegivo.afs.schedules.domain.models.createSchedule

class AddToFavoritesUseCaseImplTest : BaseTestOf<AddToFavoritesUseCase>() {
    override fun createInstance() = AddToFavoritesUseCaseImpl(favoritesRepository)

    //<editor-fold desc="mocks">
    override fun getAllMocks() = arrayOf<Any>(
        favoritesRepository
    )

    private val favoritesRepository: FavoritesRepository = mock()

    //</editor-fold>

    @Test
    fun `invoke ADDS favorites filter`() {
        val schedule = createSchedule()
        val expectedFilter = FavoriteFilter(
            groupId = schedule.groupId,
            activityId = schedule.activityId,
            dayOfWeek = schedule.getDayOfWeek(),
            timeOfDay = schedule.datetime.let {
                it.time - it.getDateWithoutTime().time
            }
        )

        given(favoritesRepository.addFilter(expectedFilter)).willReturn(Completable.complete())

        instance(schedule)
            .test().andTriggerActions()
            .assertNoErrors()
            .assertComplete()

        verify(favoritesRepository).addFilter(expectedFilter)
    }
}