/*
 * Copyright (C) 2020 Oleg Ivashchenko <olegivo@gmail.com>
 *
 * This file is part of AFS.
 *
 * AFS is free software: you can redistribute it and/or modify
 * it under the terms of the MIT License.
 *
 * AFS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * AFS.
 */

package ru.olegivo.afs.favorites.domain

import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import ru.olegivo.afs.BaseTestOf
import ru.olegivo.afs.extensions.toSingle
import ru.olegivo.afs.favorites.data.FavoritesRepositoryImpl
import ru.olegivo.afs.favorites.db.FavoriteDao
import ru.olegivo.afs.favorites.db.FavoritesDbSourceImpl
import ru.olegivo.afs.favorites.db.models.createFavoriteFilterEntity
import ru.olegivo.afs.helpers.checkSingleValue
import ru.olegivo.afs.repeat

class GetFavoritesUseCaseImplTest : BaseTestOf<GetFavoritesUseCase>() {

    override fun createInstance(): GetFavoritesUseCase {
        return GetFavoritesUseCaseImpl(
            favoritesRepository = FavoritesRepositoryImpl(
                favoritesDbSource = FavoritesDbSourceImpl(
                    favoriteDao = favoriteDao,
                    ioScheduler = testScheduler,
                    computationScheduler = testScheduler
                )
            )
        )
    }

    //<editor-fold desc="Mocks">
    private val favoriteDao: FavoriteDao = mock()

    override fun getAllMocks() = arrayOf<Any>(favoriteDao)
    //</editor-fold>

    @Test
    fun `invoke RETURNS data from db`() {
        val list = { createFavoriteFilterEntity() }.repeat(3)
        given { favoriteDao.getFavoriteFilters() }.willReturn(list.toSingle())

        instance.invoke()
            .test().andTriggerActions()
            .assertNoErrors()
            .checkSingleValue { actual ->
                assertThat(actual.map { it.activityId }).isEqualTo(list.map { it.activityId })
            }

        verify(favoriteDao).getFavoriteFilters()
    }
}
