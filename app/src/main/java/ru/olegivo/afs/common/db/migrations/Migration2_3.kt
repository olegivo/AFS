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
import ru.olegivo.afs.common.db.doInTransaction
import ru.olegivo.afs.common.db.exists

val migration2_3 = object : Migration(2, 3) {

    override fun migrate(database: SupportSQLiteDatabase) {
        database.doInTransaction {
            execSQL("ALTER TABLE favoriteFilters ADD COLUMN `group` TEXT NOT NULL DEFAULT ''")
            execSQL("ALTER TABLE favoriteFilters ADD COLUMN `activity` TEXT NOT NULL DEFAULT ''")
            val hasSchedules = exists("SELECT * FROM schedules")
            if (hasSchedules) {
                execSQL(
                    """UPDATE favoriteFilters
                            |   SET [group] = (
                            |       SELECT schedules.[group]
                            |       FROM schedules
                            |       WHERE schedules.groupId = favoriteFilters.groupId
                            |       ORDER BY datetime DESC
                            |       LIMIT 1
                            |   )
                            |;""".trimMargin()
                )
                execSQL(
                    """UPDATE favoriteFilters
                            |   SET `activity` = (
                            |       SELECT schedules.activity
                            |       FROM schedules
                            |       WHERE schedules.activityId = favoriteFilters.activityId
                            |       ORDER BY datetime DESC
                            |       LIMIT 1
                            |   )
                            |;""".trimMargin()
                )
            }
        }
    }
}
