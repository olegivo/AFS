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
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.Test
import ru.olegivo.afs.helpers.getRandomBoolean
import ru.olegivo.afs.helpers.getRandomInt
import ru.olegivo.afs.helpers.getRandomLong
import ru.olegivo.afs.helpers.getRandomString
import ru.olegivo.afs.repeat
import ru.olegivo.afs.suite.DbMigrationsTest

@DbMigrationsTest
class Migration1to2Test :
    BaseMigrationsTest(migration1_2) {

    @Test
    fun migrate_SUCCESS_WHEN_has_no_schedules() {
        prepareOld { }

        migrate()
    }

    @Test
    fun migrate_SUCCESS_WHEN_has_no_schedules_with_group_or_activity_missing_in_dictionary() {
        val groupById = { getRandomInt() }.repeat(10).associateWith { getRandomString() }
        val activityById = { getRandomInt() }.repeat(10).associateWith { getRandomString() }

        val dictionariesValues = mapOf(1 to groupById, 2 to activityById)
            .flatMap { (dictionaryId, dictionary) ->
                dictionary.map { (key, value) ->
                    ContentValues()
                        .apply {
                            put("dictionaryId", dictionaryId)
                            put("key", key)
                            put("value", value)
                        }
                }
            }

        val scheduleValues = {
            val group = groupById.entries.random()
            val activity = activityById.entries.random()

            ContentValues().apply {
                put("id", getRandomLong())
                put("clubId", getRandomInt())
                put("groupId", group.key)
                put("activityId", activity.key)
                put("datetime", getRandomLong())
                put("length", getRandomInt())
                put("preEntry", getRandomBoolean())
                put("totalSlots", getRandomInt())
                put("recordFrom", getRandomLong())
            }
        }.repeat(10)

        prepareOld {
            dictionariesValues.forEach {
                insert("dictionary", it)
            }
            scheduleValues.forEach { insert("schedules", it) }
        }

        migrate()
    }

    @Test
    fun migrate_FAILS_WHEN_has_schedules_with_group_and_activity_missing_in_dictionary() {
        val scheduleValues = {
            ContentValues().apply {
                put("id", getRandomLong())
                put("clubId", getRandomInt())
                put("groupId", getRandomInt())
                put("activityId", getRandomInt())
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

        assertThatThrownBy { migrate() }
            .hasMessage("cannot denormalize groups and activities")
    }
}
