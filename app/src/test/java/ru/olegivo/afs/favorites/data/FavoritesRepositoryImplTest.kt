package ru.olegivo.afs.favorites.data

import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.Completable
import io.reactivex.Single
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import ru.olegivo.afs.BaseTestOf
import ru.olegivo.afs.favorites.data.models.createFavoriteFilter
import ru.olegivo.afs.favorites.domain.FavoritesRepository
import ru.olegivo.afs.repeat

class FavoritesRepositoryImplTest : BaseTestOf<FavoritesRepository>() {
    override fun createInstance() = FavoritesRepositoryImpl(favoritesDbSource)

    //<editor-fold desc="mocks">
    private val favoritesDbSource: FavoritesDbSource = mock()

    override fun getAllMocks() = arrayOf<Any>(
        favoritesDbSource
    )
    //</editor-fold>

    @Test
    fun `addFilter PASSES data to favoritesDbSource`() {
        val favoriteFilter = createFavoriteFilter()

        given(favoritesDbSource.addFilter(favoriteFilter))
            .willReturn(Completable.complete())

        instance.addFilter(favoriteFilter)
            .test().andTriggerActions()
            .assertNoErrors()
            .assertComplete()

        verify(favoritesDbSource).addFilter(favoriteFilter)
    }

    @Test
    fun `removeFilter PASSES data to favoritesDbSource`() {
        val favoriteFilter = createFavoriteFilter()

        given(favoritesDbSource.removeFilter(favoriteFilter))
            .willReturn(Completable.complete())

        instance.removeFilter(favoriteFilter)
            .test().andTriggerActions()
            .assertNoErrors()
            .assertComplete()

        verify(favoritesDbSource).removeFilter(favoriteFilter)
    }

    @Test
    fun `getFavoritesScheduleIds RETURNS data from favoritesDbSource`() {
        val favoriteFilters = { createFavoriteFilter() }.repeat(10)

        given(favoritesDbSource.getFavoriteFilters()).willReturn(Single.just(favoriteFilters))

        val result = instance.getFavoriteFilters()
            .test().andTriggerActions()
            .assertNoErrors()
            .assertComplete()
            .values().single()

        assertThat(result).isEqualTo(favoriteFilters)

        verify(favoritesDbSource).getFavoriteFilters()
    }

}
