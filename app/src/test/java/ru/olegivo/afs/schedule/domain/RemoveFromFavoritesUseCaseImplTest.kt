package ru.olegivo.afs.schedule.domain

import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.Completable
import org.junit.Test
import ru.olegivo.afs.BaseTestOf
import ru.olegivo.afs.favorites.domain.FavoritesRepository
import ru.olegivo.afs.favorites.domain.models.toFavoriteFilter
import ru.olegivo.afs.schedules.domain.models.createSchedule

class RemoveFromFavoritesUseCaseImplTest : BaseTestOf<RemoveFromFavoritesUseCase>() {

    override fun createInstance() = RemoveFromFavoritesUseCaseImpl(favoritesRepository)

    //<editor-fold desc="mocks">
    private val favoritesRepository: FavoritesRepository = mock()

    override fun getAllMocks() = arrayOf<Any>()
    //</editor-fold>

    @Test
    fun `invoke WILL pass data to favoritesRepository`() {
        val schedule = createSchedule()

        val favoriteFilter = schedule.toFavoriteFilter()
        given(favoritesRepository.removeFilter(favoriteFilter))
            .willReturn(Completable.complete())

        instance.invoke(schedule)
            .test().andTriggerActions()
            .assertNoErrors()
            .assertComplete()

        verify(favoritesRepository).removeFilter(favoriteFilter)
    }
}