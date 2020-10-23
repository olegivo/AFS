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
import ru.olegivo.afs.common.add
import ru.olegivo.afs.helpers.getRandomInt
import ru.olegivo.afs.helpers.getRandomString
import java.util.Date

class Migration4to3Test :
    BaseMigrationsTest(migration4_3, 4, 3) {

    @Test
    fun migrate_SUCCESS_WHEN_has_no_favorites() {
        prepareOld { }

        migrate()
    }

    @Test
    fun migrate_SUCCESS_WHEN_has_favorites() {
        val minutes = 13 * 60 + 13

        val favoriteValues = ContentValues().apply {
            put("groupId", getRandomInt())
            put("`group`", getRandomString())
            put("activityId", getRandomInt())
            put("activity", getRandomString())
            put("dayOfWeek", getRandomInt())
            put("minutesOfDay", minutes)
        }

        prepareOld {
            insert("favoriteFilters", favoriteValues)
        }

        migrate()

        query("SELECT * FROM favoriteFilters") {
            moveToFirst()
            val time = Date(0L).add(hours = 13, minutes = 13).time
            assertThat(getInt("timeOfDay")).describedAs("$time").isEqualTo(time)
        }
    }
}
