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

val migration5_4 = object : Migration(DbVersions.v5, DbVersions.v4) {

    private val createTableFavoriteFilters_MERGE_TABLE =
        """
|   CREATE TABLE IF NOT EXISTS `favoriteFilters_MERGE_TABLE` (
|       `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
|       `groupId` INTEGER NOT NULL, 
|       `group` TEXT NOT NULL, 
|       `activityId` INTEGER NOT NULL, 
|       `activity` TEXT NOT NULL, 
|       `dayOfWeek` INTEGER NOT NULL, 
|       `minutesOfDay` INTEGER NOT NULL
|   )""".trimMargin()

    private val fillFavoriteFilters_MERGE_TABLE =
        """
|   INSERT INTO `favoriteFilters_MERGE_TABLE` (
|       `id`,
|       `groupId`,
|       `group`,
|       `activityId`,
|       `activity`,
|       `dayOfWeek`,
|       `minutesOfDay`
|   ) 
|   SELECT 
|       `favoriteFilters`.`id`,
|       `favoriteFilters`.`groupId`,
|       `favoriteFilters`.`group`,
|       `favoriteFilters`.`activityId`,
|       `favoriteFilters`.`activity`,
|       `favoriteFilters`.`dayOfWeek`,
|       `favoriteFilters`.`minutesOfDay` 
|   FROM `favoriteFilters`""".trimMargin()

    override fun migrate(database: SupportSQLiteDatabase) {
        database.doInTransaction {
            database.execSQL(createTableFavoriteFilters_MERGE_TABLE)
            database.execSQL(fillFavoriteFilters_MERGE_TABLE)
            database.execSQL("DROP TABLE IF EXISTS `favoriteFilters`")
            database.execSQL("ALTER TABLE `favoriteFilters_MERGE_TABLE` RENAME TO `favoriteFilters`")
        }
    }
}
