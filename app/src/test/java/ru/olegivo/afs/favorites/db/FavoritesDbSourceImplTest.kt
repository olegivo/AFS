package ru.olegivo.afs.favorites.db

import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.Completable
import io.reactivex.Single
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import ru.olegivo.afs.BaseTestOf
import ru.olegivo.afs.favorites.data.FavoritesDbSource
import ru.olegivo.afs.favorites.data.models.createFavoriteFilter
import ru.olegivo.afs.favorites.db.models.createFavoriteFilterEntity
import ru.olegivo.afs.favorites.db.modes.FavoriteFilterEntity
import ru.olegivo.afs.favorites.db.modes.toDb
import ru.olegivo.afs.helpers.capture
import ru.olegivo.afs.repeat

class FavoritesDbSourceImplTest : BaseTestOf<FavoritesDbSource>() {

    override fun createInstance() = FavoritesDbSourceImpl(
        favoriteDao,
        schedulerRule.testScheduler,
        schedulerRule.testScheduler
    )

    //<editor-fold desc="mocks">
    private val favoriteDao: FavoriteDao = mock()

    override fun getAllMocks() = arrayOf<Any>(
        favoriteDao
    )
    //</editor-fold>

    @Test
    fun `addFilter PASSES data to favoriteDao`() {
        val favoriteFilter = createFavoriteFilter()
        given(favoriteDao.addFilter(favoriteFilter.toDb()))
            .willReturn(Completable.complete())

        instance.addFilter(favoriteFilter)
            .test().andTriggerActions()
            .assertNoErrors()
            .assertComplete()

        val favoriteFilterEntity =
            favoriteDao.capture { param: FavoriteFilterEntity -> addFilter(param) }
        assertThat(favoriteFilterEntity.activity).isEqualTo(favoriteFilter.activity)
    }

    @Test
    fun `getFavoriteFilters RETURNS data from favoriteDao`() {
        val favoriteFilterEntities = { createFavoriteFilterEntity() }.repeat(10)

        given(favoriteDao.getFavoriteFilters()).willReturn(Single.just(favoriteFilterEntities))

        val result = instance.getFavoriteFilters()
            .test().andTriggerActions()
            .assertNoErrors()
            .assertComplete()
            .values().single()

        assertThat(result).extracting<String> { it.activity }
            .containsExactlyElementsOf(favoriteFilterEntities.map { it.activity })

        verify(favoriteDao).getFavoriteFilters()
    }
}