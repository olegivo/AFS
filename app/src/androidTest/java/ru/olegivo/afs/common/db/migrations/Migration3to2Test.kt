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
import org.junit.Test
import ru.olegivo.afs.favorites.data.models.createFavoriteFilter
import ru.olegivo.afs.favorites.domain.models.FavoriteFilter
import ru.olegivo.afs.repeat
import ru.olegivo.afs.suite.DbMigrationsTest

@DbMigrationsTest
class Migration3to2Test :
    BaseMigrationsTest(migration3_2, 3, 2) {

    @Test
    fun migrate_SUCCESS_WHEN_has_no_favorites() {
        prepareOld { }

        migrate()
    }

    @Test
    fun migrate_SUCCESS_WHEN_has_favorites() {
        val favorites = { createFavoriteFilter() }.repeat(10)
        val favoriteValues = favorites
            .map {
                ContentValues().apply {
                    put("groupId", it.groupId)
                    put("`group`", it.group)
                    put("activityId", it.activityId)
                    put("activity", it.activity)
                    put("dayOfWeek", it.dayOfWeek)
                    put("timeOfDay", it.minutesOfDay)
                }
            }

        prepareOld {
            favoriteValues.forEach { insert("favoriteFilters", it) }
        }

        migrate()

        val result = mutableListOf<FavoriteFilter>()
        query("SELECT * FROM favoriteFilters") {
            while (moveToNext()) {
                result.add(
                    FavoriteFilter(
                        groupId = getInt("groupId"),
                        group = "",
                        activityId = getInt("activityId"),
                        activity = "",
                        dayOfWeek = getInt("dayOfWeek"),
                        minutesOfDay = getInt("timeOfDay")
                    )
                )
            }
        }
        val expected = favorites.map { it.copy(group = "", activity = "") }
        assertThat(expected).isEqualTo(result)
    }
}
