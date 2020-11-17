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
import ru.olegivo.afs.helpers.getRandomInt
import ru.olegivo.afs.helpers.getRandomString
import ru.olegivo.afs.suite.DbMigrationsTest

@DbMigrationsTest
class Migration5to4Test :
    BaseMigrationsTest(migration5_4) {

    @Test
    fun migrate_SUCCESS_WHEN_has_no_favorites() {
        prepareOld { }

        migrate()
    }

    @Test
    fun migrate_SUCCESS_WHEN_has_favorites() {
        val favoriteValues = ContentValues().apply {
            put("id", getRandomInt())
            put("clubId", getRandomInt())
            put("groupId", getRandomInt())
            put("`group`", getRandomString())
            put("activityId", getRandomInt())
            put("activity", getRandomString())
            put("dayOfWeek", getRandomInt())
            put("minutesOfDay", getRandomInt())
        }

        prepareOld {
            insert("favoriteFilters", favoriteValues)
        }

        migrate()

        query("SELECT * FROM favoriteFilters") {
            moveToFirst()
            assertThat(columnNames)
                .doesNotContain("clubId")
                .hasSize(7)

            for (columnName in (favoriteValues.keySet().toList() - "clubId")) {
                val originValue = favoriteValues[columnName]
                val dbColumnName = columnName.replace("`", "")
                val value = when (originValue) {
                    is Int -> getInt(dbColumnName)
                    is Long -> getLong(dbColumnName)
                    is String -> getString(dbColumnName)
                    else -> TODO("unknown type of $originValue in column $dbColumnName")
                }
                assertThat(value).isEqualTo(originValue)
            }
        }
    }
}
