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

private val createTableDictionary =
    """CREATE TABLE IF NOT EXISTS `dictionary` (
    |`dictionaryId` INTEGER NOT NULL, 
    |`key` INTEGER NOT NULL, 
    |`value` TEXT NOT NULL, 
    |PRIMARY KEY(`dictionaryId`, `key`)
    |)"""
        .trimMargin()

private val insertGroups =
    """INSERT INTO dictionary
    |SELECT 1, groupId, [group]
    |FROM schedules
    |GROUP BY groupId, [group]
    |;""".trimMargin()

private val insertActivities =
    """INSERT INTO dictionary
    |SELECT 2, activityId, activity
    |FROM schedules
    |GROUP BY activityId, activity
    |;""".trimMargin()

private val createTableSchedules_1 =
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

private val fillSchedules_1 =
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

private val createIndex_schedules_datetime_clubId =
    """CREATE INDEX IF NOT EXISTS `index_schedules_datetime_clubId`
        |ON `schedules` (`datetime`, `clubId`)""".trimMargin()

val migration2_1 = object : Migration(DbVersions.v2, DbVersions.v1) {

    override fun migrate(database: SupportSQLiteDatabase) {
        database.doInTransaction {
            val hasSchedules = exists("SELECT * FROM schedules")
            execSQL(createTableDictionary)
            if (hasSchedules) {
                execSQL(insertGroups)
                execSQL(insertActivities)
            }
            execSQL(createTableSchedules_1)
            execSQL(fillSchedules_1)
            execSQL("DROP table schedules")
            execSQL("ALTER TABLE schedules_1 RENAME TO schedules")
            execSQL(
                createIndex_schedules_datetime_clubId
            )
        }
    }
}
