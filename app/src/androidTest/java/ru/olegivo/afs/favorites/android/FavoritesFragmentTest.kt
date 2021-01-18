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

import org.junit.Test
import ru.olegivo.afs.ExternalDependencies
import ru.olegivo.afs.common.android.BaseIntegratedIsolatedUITest
import ru.olegivo.afs.common.date
import ru.olegivo.afs.favorites.db.models.toDb
import ru.olegivo.afs.favorites.domain.models.FavoriteFilter
import ru.olegivo.afs.favorites.presentation.models.FavoritesItem
import ru.olegivo.afs.helpers.getRandomInt
import ru.olegivo.afs.helpers.getRandomLong
import ru.olegivo.afs.shared.schedules.db.models.ScheduleEntity
import ru.olegivo.afs.suite.IntegratedIsolatedUITest
import java.util.Calendar

@IntegratedIsolatedUITest
class FavoritesFragmentTest :
    BaseIntegratedIsolatedUITest<FavoritesFragmentFixture, FavoritesFragmentScreen>() {

    override fun createFixture(externalDependencies: ExternalDependencies) =
        FavoritesFragmentFixture(externalDependencies)

    @Test
    fun shows_empty_state_when_has_no_favorites() {
        fixture.prepare(emptyList())

        fixture.screen {
            assertScreenShown()
            assertItemsCount(0)
        }
    }

    @Test
    fun shows_favorites_when_has_favorites() {
        val clubId = getRandomInt()
        val items = listOf(
            FavoritesItem(
                FavoriteFilter(
                    clubId = clubId,
                    groupId = getRandomInt(),
                    group = "Игровые виды спорта 4",
                    activityId = getRandomInt(),
                    activity = "Волейбол клиенты 4",
                    dayOfWeek = Calendar.SATURDAY,
                    minutesOfDay = 60 * 20
                ),
                "сб, 20:00"
            ),
            FavoritesItem(
                FavoriteFilter(
                    clubId = clubId,
                    groupId = getRandomInt(),
                    group = "Игровые виды спорта 3",
                    activityId = getRandomInt(),
                    activity = "Волейбол клиенты 3",
                    dayOfWeek = Calendar.SUNDAY,
                    minutesOfDay = 60 * 21
                ),
                "вс, 21:00"
            ),
            FavoritesItem(
                FavoriteFilter(
                    clubId = clubId,
                    groupId = getRandomInt(),
                    group = "Игровые виды спорта 2",
                    activityId = getRandomInt(),
                    activity = "Волейбол клиенты 2",
                    dayOfWeek = Calendar.TUESDAY,
                    minutesOfDay = 60 * 12
                ),
                "вт, 12:00"
            ),
            FavoritesItem(
                FavoriteFilter(
                    clubId = clubId,
                    groupId = getRandomInt(),
                    group = "Игровые виды спорта 1",
                    activityId = getRandomInt(),
                    activity = "Волейбол клиенты 1",
                    dayOfWeek = Calendar.MONDAY,
                    minutesOfDay = 60 * 11
                ),
                "пн, 11:00"
            )
        )

        fixture.prepare(items.mapIndexed { i, it -> it.filter.toDb().copy(id = i) })

        fixture.screen {
            assertScreenShown()
            assertItemsCount(items.size)
            assertItemsShown(items)
        }
    }

    @Test
    fun favorite_item_click_navigates_to_closest_schedule() {
        val filter = FavoriteFilter(
            clubId = getRandomInt(),
            groupId = getRandomInt(),
            group = "Игровые виды спорта 4",
            activityId = getRandomInt(),
            activity = "Волейбол клиенты 4",
            dayOfWeek = Calendar.SATURDAY,
            minutesOfDay = 60 * 20
        )
        val favoritesItem = FavoritesItem(
            filter = filter,
            duty = "сб, 20:00"
        )

        fixture.prepare(listOf(favoritesItem.filter.toDb().copy(id = getRandomInt())))
        val scheduleEntity = ScheduleEntity(
            id = getRandomLong(),
            clubId = filter.clubId,
            groupId = filter.groupId,
            group = filter.group,
            activityId = filter.activityId,
            activity = filter.activity,
            datetime = date(2020, 11, 19, 20, 0),
            length = 90,
            preEntry = true,
            totalSlots = 1,
            recordFrom = null,
            recordTo = null
        )

        fixture.screen {
            assertScreenShown()
            assertItemsCount(1)
            assertItemsShown(listOf(favoritesItem))
        }

        fixture.prepareItemClick(favoritesItem.filter, scheduleEntity)
        fixture.screen {
            clickOnItem(favoritesItem)
        }

        fixture.checkItemClick(
//            favoriteFilter = favoritesItem.filter,
//            clubId = filter.clubId,
            scheduleEntity = scheduleEntity
//            fio = "",
//            phone = ""
        )
    }
}
