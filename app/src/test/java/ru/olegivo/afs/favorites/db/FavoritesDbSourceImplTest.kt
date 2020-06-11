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

package ru.olegivo.afs.favorites.db

import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.willReturn
import io.reactivex.Completable
import io.reactivex.Single
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import ru.olegivo.afs.BaseTestOf
import ru.olegivo.afs.favorites.data.FavoritesDbSource
import ru.olegivo.afs.favorites.data.models.createFavoriteFilter
import ru.olegivo.afs.favorites.db.models.createFavoriteFilterEntity
import ru.olegivo.afs.favorites.db.models.FavoriteFilterEntity
import ru.olegivo.afs.favorites.db.models.toDb
import ru.olegivo.afs.favorites.domain.models.toFavoriteFilter
import ru.olegivo.afs.helpers.capture
import ru.olegivo.afs.helpers.getRandomBoolean
import ru.olegivo.afs.repeat
import ru.olegivo.afs.schedules.domain.models.createSchedule

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
        assertThat(favoriteFilterEntity.activityId).isEqualTo(favoriteFilter.activityId)
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

        assertThat(result).extracting<Int> { it.activityId }
            .containsExactlyElementsOf(favoriteFilterEntities.map { it.activityId })

        verify(favoriteDao).getFavoriteFilters()
    }

    @Test
    fun `exist RETURNS data from favoriteDao`() {
        val favoriteFilter = createSchedule().toFavoriteFilter()
        val exist = getRandomBoolean()
        with(favoriteFilter) {
            given(
                favoriteDao.exist(
                    groupId = groupId,
                    activityId = activityId,
                    dayOfWeek = dayOfWeek,
                    timeOfDay = timeOfDay
                )
            )
                .willReturn(Single.just(exist))
        }

        val result = instance.exist(favoriteFilter)
            .test().andTriggerActions()
            .assertNoErrors()
            .assertComplete()
            .values().single()

        with(favoriteFilter) {
            verify(favoriteDao).exist(
                groupId = groupId,
                activityId = activityId,
                dayOfWeek = dayOfWeek,
                timeOfDay = timeOfDay
            )
        }
        assertThat(result).isEqualTo(exist)
    }

    @Test
    fun `hasPlannedReminderToRecord RETURNS data from favoriteDao`() {
        val schedule = createSchedule()
        val hasPlannedReminderToRecord = getRandomBoolean()
        given { favoriteDao.hasPlannedReminderToRecord(schedule.id) }
            .willReturn { Single.just(hasPlannedReminderToRecord) }

        instance.hasPlannedReminderToRecord(schedule)
            .assertResult {
                assertThat(it).isEqualTo(hasPlannedReminderToRecord)
            }

        verify(favoriteDao).hasPlannedReminderToRecord(schedule.id)
    }
}
