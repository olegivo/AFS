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

package ru.olegivo.afs.favorites.data

import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.willReturn
import io.reactivex.Completable
import io.reactivex.Single
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import ru.olegivo.afs.BaseTestOf
import ru.olegivo.afs.favorites.data.models.createFavoriteFilter
import ru.olegivo.afs.favorites.domain.FavoritesRepository
import ru.olegivo.afs.favorites.domain.models.toFavoriteFilter
import ru.olegivo.afs.helpers.getRandomBoolean
import ru.olegivo.afs.helpers.getRandomDate
import ru.olegivo.afs.helpers.getRandomLong
import ru.olegivo.afs.repeat
import ru.olegivo.afs.schedules.domain.models.createSchedule

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
            .willReturn { Completable.complete() }

        instance.addFilter(favoriteFilter)
            .assertSuccess()

        verify(favoritesDbSource).addFilter(favoriteFilter)
    }

    @Test
    fun `removeFilter PASSES data to favoritesDbSource`() {
        val favoriteFilter = createFavoriteFilter()

        given(favoritesDbSource.removeFilter(favoriteFilter))
            .willReturn { Completable.complete() }

        instance.removeFilter(favoriteFilter)
            .assertSuccess()

        verify(favoritesDbSource).removeFilter(favoriteFilter)
    }

    @Test
    fun `getFavoritesScheduleIds RETURNS data from favoritesDbSource`() {
        val favoriteFilters = { createFavoriteFilter() }.repeat(10)

        given(favoritesDbSource.getFavoriteFilters()).willReturn { Single.just(favoriteFilters) }

        instance.getFavoriteFilters()
            .assertResult {
                assertThat(it).isEqualTo(favoriteFilters)
            }

        verify(favoritesDbSource).getFavoriteFilters()
    }

    @Test
    fun `isFavorite RETURNS data from favoritesDbSource`() {
        val schedule = createSchedule()
        val isFavorite = getRandomBoolean()
        given(favoritesDbSource.exist(schedule.toFavoriteFilter()))
            .willReturn { Single.just(isFavorite) }

        instance.isFavorite(schedule)
            .assertResult {
                assertThat(it).isEqualTo(isFavorite)
            }

        verify(favoritesDbSource).exist(schedule.toFavoriteFilter())
    }

    @Test
    fun `addReminderToRecord PASSES data to favoritesDbSource`() {
        val scheduleId = getRandomLong()
        val dateFrom = getRandomDate()
        val dateUntil = getRandomDate()
        given(
            favoritesDbSource.addReminderToRecord(
                scheduleId = scheduleId,
                dateFrom = dateFrom,
                dateUntil = dateUntil
            )
        )
            .willReturn { Completable.complete() }

        instance.addReminderToRecord(scheduleId, dateFrom, dateUntil)
            .assertSuccess()

        verify(favoritesDbSource).addReminderToRecord(
            scheduleId = scheduleId,
            dateFrom = dateFrom,
            dateUntil = dateUntil
        )
    }

    @Test
    fun `hasPlannedReminderToRecord RETURNS data from favoritesDbSource`() {
        val schedule = createSchedule()
        val hasReminder = getRandomBoolean()

        given(favoritesDbSource.hasPlannedReminderToRecord(schedule))
            .willReturn { Single.just(hasReminder) }

        instance.hasPlannedReminderToRecord(schedule)
            .assertResult {
                assertThat(it).isEqualTo(hasReminder)
            }

        verify(favoritesDbSource).hasPlannedReminderToRecord(schedule)
    }
}
