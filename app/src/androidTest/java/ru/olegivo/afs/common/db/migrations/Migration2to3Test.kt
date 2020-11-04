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
import org.assertj.core.api.Assertions.fail
import org.junit.Test
import ru.olegivo.afs.favorites.data.models.createFavoriteFilter
import ru.olegivo.afs.helpers.getRandomBoolean
import ru.olegivo.afs.helpers.getRandomInt
import ru.olegivo.afs.helpers.getRandomLong
import ru.olegivo.afs.repeat
import ru.olegivo.afs.suite.DbMigrationsTest

@DbMigrationsTest
class Migration2to3Test :
    BaseMigrationsTest(migration2_3, 2, 3) {

    @Test
    fun migrate_SUCCESS_WHEN_has_no_schedules() {
        prepareOld { }

        migrate()
    }

    @Test
    fun migrate_SUCCESS_WHEN_has_no_favorites_with_group_or_activity_missing_in_schedules() {
        val earlierG1A2 = createFavoriteFilter().copy(minutesOfDay = 1, groupId = 1, activityId = 2)
        val earlierG2A1 = createFavoriteFilter().copy(minutesOfDay = 1, groupId = 2, activityId = 1)
        val laterG1A2 = createFavoriteFilter().copy(minutesOfDay = 2, groupId = 1, activityId = 2)
        val laterG2A1 = createFavoriteFilter().copy(minutesOfDay = 2, groupId = 2, activityId = 1)
        val favorites = listOf(
            earlierG1A2,
            earlierG2A1,
            laterG1A2,
            laterG2A1
        )
        val scheduleValues = favorites
            .map {
                ContentValues().apply {
                    put("id", getRandomInt())
                    put("clubId", getRandomInt())
                    put("groupId", it.groupId)
                    put("`group`", it.group)
                    put("activityId", it.activityId)
                    put("activity", it.activity)
                    put("datetime", it.minutesOfDay)
                    put("length", getRandomInt())
                    put("preEntry", getRandomBoolean())
                    put("totalSlots", getRandomInt())
                    put("recordFrom", getRandomLong())
                }
            }
        val favoriteValues = favorites
            .mapIndexed { i, it ->
                ContentValues().apply {
                    put("id", i + 1)
                    put("groupId", it.groupId)
                    put("activityId", it.activityId)
                    put("dayOfWeek", getRandomInt())
                    put("timeOfDay", getRandomLong())
                }
            }

        prepareOld {
            scheduleValues.forEach { insert("schedules", it) }
            favoriteValues.forEach { insert("favoriteFilters", it) }
        }

        migrate()

        query("select * from favoriteFilters") {
            while (moveToNext()) {
                when (getInt("groupId")) {
                    1 -> assertThat(getString("group")).isEqualTo(laterG1A2.group)
                    2 -> assertThat(getString("group")).isEqualTo(laterG2A1.group)
                    else -> fail("Unexpected groupId")
                }
                when (getInt("activityId")) {
                    1 -> assertThat(getString("activity")).isEqualTo(laterG2A1.activity)
                    2 -> assertThat(getString("activity")).isEqualTo(laterG1A2.activity)
                    else -> fail("Unexpected groupId")
                }
            }
        }
        closeDb()
    }

    @Test
    fun migrate_SUCCESS_with_empty_values_WHEN_has_favorites_with_groupId_and_activityId_missing_in_schedules() {
        val favoriteValues = {
            ContentValues().apply {
                put("groupId", getRandomInt())
                put("activityId", getRandomInt())
                put("dayOfWeek", getRandomInt())
                put("timeOfDay", getRandomLong())
            }
        }.repeat(10)

        prepareOld {
            favoriteValues.forEach { insert("favoriteFilters", it) }
        }

        migrate()

        query("select * from favoriteFilters") {
            while (moveToNext()) {
                assertThat(getString("group")).isEmpty()
                assertThat(getString("activity")).isEmpty()
            }
        }
        closeDb()
    }
}
