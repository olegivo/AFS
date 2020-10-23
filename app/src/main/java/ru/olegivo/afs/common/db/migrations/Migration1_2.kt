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

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import ru.olegivo.afs.common.db.DbVersions
import ru.olegivo.afs.common.db.doInTransaction
import ru.olegivo.afs.common.db.exists

val migration1_2 = object : Migration(DbVersions.v1, DbVersions.v2) {

    override fun migrate(database: SupportSQLiteDatabase) {
        database.doInTransaction {
            val hasInvalidRecords = exists(
                """SELECT * FROM schedules s
LEFT JOIN dictionary as groups on s.groupId = groups.key and groups.dictionaryId = 1 
LEFT JOIN dictionary as activities on s.activityId = activities.key and activities.dictionaryId = 2
WHERE groups.key IS NULL OR activities.key IS NULL 
"""
            )
            if (hasInvalidRecords) throw IllegalStateException("cannot denormalize groups and activities")

            execSQL("ALTER TABLE schedules ADD COLUMN `group` TEXT NOT NULL DEFAULT ''")
            execSQL("ALTER TABLE schedules ADD COLUMN `activity` TEXT NOT NULL DEFAULT ''")
            val hasSchedules = exists("SELECT * FROM schedules")
            if (hasSchedules) {
                execSQL(
                    """UPDATE schedules
                            |   SET [group] = (
                            |       SELECT dictionary.value
                            |       FROM dictionary
                            |       WHERE dictionary.dictionaryId = 1 AND schedules.groupId = dictionary.key
                            |   )
                            |;""".trimMargin()
                )
                execSQL(
                    """UPDATE schedules
                            |   SET `activity` = (
                            |       SELECT dictionary.value
                            |       FROM dictionary
                            |       WHERE dictionary.dictionaryId = 2 AND schedules.activityId = dictionary.key
                            |   )
                            |;""".trimMargin()
                )
            }
            execSQL(
                """DROP TABLE dictionary"""
            )
        }
    }
}
