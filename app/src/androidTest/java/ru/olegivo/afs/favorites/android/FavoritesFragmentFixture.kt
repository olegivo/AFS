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

package ru.olegivo.afs.favorites.android

import com.nhaarman.mockitokotlin2.verifyBlocking
import ru.olegivo.afs.BaseFixture
import ru.olegivo.afs.ExternalDependencies
import ru.olegivo.afs.common.android.ChainRuleHolder
import ru.olegivo.afs.favorites.db.models.FavoriteFilterEntity
import ru.olegivo.afs.favorites.domain.models.FavoriteFilter
import ru.olegivo.afs.helpers.givenBlocking
import ru.olegivo.afs.helpers.willReturn
import ru.olegivo.afs.home.android.HomeFragmentFixture
import ru.olegivo.afs.schedules.db.models.ScheduleEntity

class FavoritesFragmentFixture(
    externalDependencies: ExternalDependencies,
    private val homeFragmentFixture: HomeFragmentFixture = HomeFragmentFixture(externalDependencies)
) : BaseFixture<FavoritesFragmentScreen>(externalDependencies, FavoritesFragmentScreen),
    ChainRuleHolder by homeFragmentFixture {

    fun prepare(filters: List<FavoriteFilterEntity>) {
        withFakeDatabase {
            action { favorites.insert(*filters.toTypedArray()) }
        }
        homeFragmentFixture.screen {
            clickFavoritesButton()
        }
        triggerActions()
    }

    fun prepareItemClick(
        favoriteFilter: FavoriteFilter,
        scheduleEntity: ScheduleEntity
    ) {
        withFakeDatabase {
            action { schedules.insert(scheduleEntity) }
        }
//        given { scheduleDao.filterSchedules(favoriteFilter, filter.clubId) }
//            .willReturn(listOf(scheduleEntity).toSingle())
//        given { scheduleDao.getSchedule(scheduleEntity.id) }
//            .willReturn(scheduleEntity.toSingle())
        givenBlocking(api) {
            getSlots(
                favoriteFilter.clubId,
                mapOf("0" to scheduleEntity.id.toString())
            )
        }
            .willReturn { emptyList() }
//        given { reserveDao.isScheduleReserved(scheduleEntity.id) }
//            .willReturn(true.toSingle())
//        given {
//            favoriteDao.exist(
//                favoriteFilter.groupId,
//                favoriteFilter.activityId,
//                favoriteFilter.dayOfWeek,
//                favoriteFilter.minutesOfDay
//            )
//        }.willReturn(true.toSingle())
        triggerActions()
    }

    fun checkItemClick(
//        favoriteFilter: FavoriteFilter,
//        clubId: Int,
        scheduleEntity: ScheduleEntity
//        fio: String,
//        phone: String
    ) {
//        verify(afsDatabase).favorites
//        verify(afsDatabase, times(2)).schedules
//        verify(afsDatabase).reserve
//        verify(scheduleDao).filterSchedules(favoriteFilter, clubId)
//        verify(scheduleDao).getSchedule(scheduleEntity.id)
        verifyBlocking(api) { getSlots(scheduleEntity.clubId, mapOf("0" to scheduleEntity.id.toString())) }
//        verify(reserveDao).isScheduleReserved(scheduleEntity.id)
//        verify(favoriteDao).exist(
//            favoriteFilter.groupId,
//            favoriteFilter.activityId,
//            favoriteFilter.dayOfWeek,
//            favoriteFilter.minutesOfDay
//        )
    }
}
