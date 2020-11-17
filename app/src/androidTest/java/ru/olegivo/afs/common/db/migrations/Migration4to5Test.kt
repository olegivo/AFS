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

package ru.olegivo.afs.common.db.migrations

import android.content.ContentValues
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import ru.olegivo.afs.common.add
import ru.olegivo.afs.common.date
import ru.olegivo.afs.favorites.data.models.createFavoriteFilter
import ru.olegivo.afs.favorites.domain.models.FavoriteFilter
import ru.olegivo.afs.helpers.getRandomBoolean
import ru.olegivo.afs.helpers.getRandomInt
import ru.olegivo.afs.helpers.getRandomLong
import ru.olegivo.afs.repeat
import ru.olegivo.afs.suite.DbMigrationsTest
import java.util.Calendar

@DbMigrationsTest
class Migration4to5Test :
    BaseMigrationsTest(migration4_5) {

    @Before
    fun setUp() {
        MigrationsEnvironment.predefinedDate = null
    }

    @Test
    fun migrate_SUCCESS_WHEN_has_no_favorites() {
        prepareOld { }

        migrate()
    }

    @Test
    fun migrate_UPDATES_clubId_WHEN_has_favorites_and_schedules_with_single_club() {
        val hours = 21
        val minutes = 30
        val referDate = date(years = 2020, months = 10, days = 20, hours = hours, minutes = minutes)
        val clubId = getRandomInt()

        val filter = createFavoriteFilter().copy(
            groupId = 1,
            activityId = 2,
            dayOfWeek = Calendar.FRIDAY,
            minutesOfDay = hours * 60 + minutes
        )
        val favorites = listOf(filter)
        prepare(favorites) { _, favoriteFilter ->
            put("clubId", clubId)
            put("groupId", favoriteFilter.groupId)
            put("`group`", favoriteFilter.group)
            put("activityId", favoriteFilter.activityId)
            put("activity", favoriteFilter.activity)
            put("datetime", referDate.add(days = 7 * getRandomInt(from = 0, until = 4)).time)
        }

        migrate()

        query("SELECT * FROM favoriteFilters") {
            moveToFirst()
            assertThat(getInt("clubId")).isEqualTo(clubId)
        }
    }

    @Test
    fun migrate_UPDATES_clubId_with_closest_date_WHEN_has_favorites_and_schedules_with_multiple_clubs() {
        val hours = 21
        val minutes = 30
        val referDate = date(years = 2020, months = 10, days = 20, hours = hours, minutes = minutes)

        val filter = createFavoriteFilter().copy(
            groupId = 1,
            activityId = 2,
            dayOfWeek = Calendar.FRIDAY,
            minutesOfDay = hours * 60 + minutes
        )
        val favorites = listOf(filter)

        MigrationsEnvironment.predefinedDate = referDate
        val dateByClub = (1..3).associateWith { referDate.add(days = 7 * it).time }

        prepare(favorites) { index, favoriteFilter ->
            val clubId = index + 1
            put("clubId", clubId)
            put("groupId", favoriteFilter.groupId)
            put("`group`", favoriteFilter.group)
            put("activityId", favoriteFilter.activityId)
            put("activity", favoriteFilter.activity)
            put("datetime", dateByClub[clubId])
        }

        migrate()

        query("SELECT * FROM favoriteFilters") {
            moveToFirst()
            assertThat(getInt("clubId")).isEqualTo(1)
        }
    }

    @Test
    fun migrate_DELETES_favorite_WHEN_has_favorites_and_schedules_from_several_clubs_not_matched_by_datetime() {
        val hours = 21
        val minutes = 30

        val filter = createFavoriteFilter().copy(
            groupId = 1,
            activityId = 2,
            dayOfWeek = Calendar.FRIDAY,
            minutesOfDay = hours * 60 + minutes
        )
        val favorites = listOf(filter)

        prepare(favorites) { _, favoriteFilter ->
            put("clubId", getRandomInt())
            put("groupId", favoriteFilter.groupId)
            put("`group`", favoriteFilter.group)
            put("activityId", favoriteFilter.activityId)
            put("activity", favoriteFilter.activity)
            put("datetime", getRandomLong())
        }

        migrate()

        query("SELECT * FROM favoriteFilters") {
            assertThat(moveToNext()).isFalse()
        }
    }

    @Test
    fun migrate_DELETES_favorite_WHEN_has_favorites_and_schedules_not_matched_by_activity_and_group() {
        val hours = 21
        val minutes = 30
        val clubId = getRandomInt()
        val referDate = date(years = 2020, months = 10, days = 20, hours = hours, minutes = minutes)

        val filter = createFavoriteFilter().copy(
            groupId = 1,
            activityId = 2,
            dayOfWeek = Calendar.FRIDAY,
            minutesOfDay = hours * 60 + minutes
        )
        val favorites = listOf(filter)

        prepare(favorites) { _, favoriteFilter ->
            put("clubId", clubId)
            put("groupId", getRandomInt())
            put("`group`", favoriteFilter.group)
            put("activityId", getRandomInt())
            put("activity", favoriteFilter.activity)
            put("datetime", referDate.add(days = 7 * getRandomInt(from = 0, until = 4)).time)
        }

        migrate()

        query("SELECT * FROM favoriteFilters") {
            assertThat(count).isEqualTo(0)
        }
    }

    private fun prepare(
        favorites: List<FavoriteFilter>,
        schedulesPerFavorite: Int = 3,
        scheduleBuilder: ContentValues.(Int, FavoriteFilter) -> Unit
    ) {
        val scheduleValues = favorites
            .flatMapIndexed { index, filter ->
                {
                    ContentValues()
                        .apply {
                            put("id", getRandomInt())
                            put("length", getRandomInt())
                            put("preEntry", getRandomBoolean())
                            put("totalSlots", getRandomInt())
                            put("recordFrom", getRandomLong())
                            scheduleBuilder(index, filter)
                        }
                }.repeat(schedulesPerFavorite)
            }
        val favoriteValues = favorites
            .mapIndexed { i, it ->
                ContentValues().apply {
                    put("id", i + 1)
                    put("groupId", it.groupId)
                    put("`group`", it.group)
                    put("activityId", it.activityId)
                    put("activity", it.activity)
                    put("dayOfWeek", it.dayOfWeek)
                    put("minutesOfDay", it.minutesOfDay)
                }
            }

        prepareOld {
            scheduleValues.forEach { insert("schedules", it) }
            favoriteValues.forEach { insert("favoriteFilters", it) }
        }
    }
}
