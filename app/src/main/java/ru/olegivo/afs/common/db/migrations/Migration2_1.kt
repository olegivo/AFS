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

val migration2_1 = object : Migration(2, 1) {

    override fun migrate(database: SupportSQLiteDatabase) {
        database.doInTransaction {
            val hasSchedules = exists("SELECT * FROM schedules")
            execSQL(
                """CREATE TABLE IF NOT EXISTS `dictionary` (
                    |`dictionaryId` INTEGER NOT NULL, 
                    |`key` INTEGER NOT NULL, 
                    |`value` TEXT NOT NULL, 
                    |PRIMARY KEY(`dictionaryId`, `key`)
                    |)"""
                    .trimMargin()
            )
            if (hasSchedules) {
                execSQL(
                    """INSERT INTO dictionary
                        |SELECT 1, groupId, [group]
                        |FROM schedules
                        |GROUP BY groupId, [group]
                        |;""".trimMargin()
                )
                execSQL(
                    """INSERT INTO dictionary
                        |SELECT 2, activityId, activity
                        |FROM schedules
                        |GROUP BY activityId, activity
                        |;""".trimMargin()
                )
            }
            execSQL(
                """CREATE TABLE IF NOT EXISTS `schedules_1` (
                    |`id` INTEGER NOT NULL, 
                    |`clubId` INTEGER NOT NULL, 
                    |`groupId` INTEGER NOT NULL, 
                    |`activityId` INTEGER NOT NULL, 
                    |`datetime` INTEGER NOT NULL, 
                    |`length` INTEGER NOT NULL, 
                    |`preEntry` INTEGER NOT NULL, 
                    |`totalSlots` INTEGER, 
                    |`recordFrom` INTEGER, 
                    |`recordTo` INTEGER, 
                    |PRIMARY KEY(`id`)
                    |)"""
                    .trimMargin()
            )
            execSQL(
                """INSERT INTO `schedules_1`
                    |SELECT
                    |id, 
                    |clubId, 
                    |groupId, 
                    |activityId, 
                    |datetime, 
                    |length, 
                    |preEntry, 
                    |totalSlots, 
                    |recordFrom, 
                    |recordTo 
                    |FROM schedules"""
                    .trimMargin()
            )
            execSQL("DROP table schedules")
            execSQL("ALTER TABLE schedules_1 RENAME TO schedules")
            execSQL("CREATE INDEX IF NOT EXISTS `index_schedules_datetime_clubId` ON `schedules` (`datetime`, `clubId`)")
        }
    }
}