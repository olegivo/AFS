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
import ru.olegivo.afs.helpers.getRandomBoolean
import ru.olegivo.afs.helpers.getRandomInt
import ru.olegivo.afs.helpers.getRandomLong
import ru.olegivo.afs.helpers.getRandomString
import ru.olegivo.afs.repeat

class Migration2to1Test :
    BaseMigrationsTest(migration2_1, 2, 1) {

    @Test
    fun migrate_SUCCESS_WHEN_has_no_schedules() {
        prepareOld { }

        migrate()
    }

    @Test
    fun migrate_SUCCESS_WHEN_has_schedules() {
        val groupById = { getRandomInt() }.repeat(10).associateWith { getRandomString() }
        val activityById = { getRandomInt() }.repeat(10).associateWith { getRandomString() }

        val expectedGroups = mutableMapOf<Int, String>()
        val expectedActivities = mutableMapOf<Int, String>()

        val scheduleValues = {
            val group = groupById.entries.random()
            expectedGroups.putIfAbsent(group.key, group.value)
            val activity = activityById.entries.random()
            expectedActivities.putIfAbsent(activity.key, activity.value)

            ContentValues().apply {
                put("id", getRandomLong())
                put("clubId", getRandomInt())
                put("groupId", group.key)
                put("[group]", group.value)
                put("activityId", activity.key)
                put("activity", activity.value)
                put("datetime", getRandomLong())
                put("length", getRandomInt())
                put("preEntry", getRandomBoolean())
                put("totalSlots", getRandomInt())
                put("recordFrom", getRandomLong())
            }
        }.repeat(10)

        prepareOld {
            scheduleValues.forEach { insert("schedules", it) }
        }

        migrate()

        db.query("SELECT * FROM dictionary").use {
            val actualGroups = mutableMapOf<Int, String>()
            val actualActivities = mutableMapOf<Int, String>()

            while (it.moveToNext()) {
                val actualMap = when (it.getInt(it.getColumnIndex("dictionaryId"))) {
                    1 -> actualGroups
                    2 -> actualActivities
                    else -> TODO()
                }
                val key = it.getInt(it.getColumnIndex("key"))
                val value = it.getString(it.getColumnIndex("value"))
                actualMap += key to value
            }

            assertThat(actualGroups.keys).containsExactlyInAnyOrderElementsOf(expectedGroups.keys)
            assertThat(actualActivities.keys).containsExactlyInAnyOrderElementsOf(expectedActivities.keys)
            actualGroups.keys.forEach { key ->
                assertThat(actualGroups[key]).isEqualTo(expectedGroups[key])
            }
            actualGroups.keys.forEach { key ->
                assertThat(actualActivities[key]).isEqualTo(expectedActivities[key])
            }
        }
    }

}