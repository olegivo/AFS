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

private val createTableFavoriteFilters_1 =
    """
|   CREATE TABLE IF NOT EXISTS `favoriteFilters_1` ( 
|       `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
|       `groupId` INTEGER NOT NULL, 
|       `activityId` INTEGER NOT NULL, 
|       `dayOfWeek` INTEGER NOT NULL, 
|       `timeOfDay` INTEGER NOT NULL
|       )""".trimMargin()

private val fillFavoriteFilters_1 =
    """INSERT INTO `favoriteFilters_1` (
    |   id,
    |   groupId,
    |   activityId,
    |   dayOfWeek,
    |   timeOfDay
    |)
    |SELECT
    |   id,
    |   groupId,
    |   activityId,
    |   dayOfWeek,
    |   timeOfDay
    |FROM favoriteFilters""".trimMargin()

val migration3_2 = object : Migration(DbVersions.v3, DbVersions.v2) {

    override fun migrate(database: SupportSQLiteDatabase) {
        database.doInTransaction {
            execSQL(createTableFavoriteFilters_1)
            execSQL(fillFavoriteFilters_1)
            execSQL("DROP table favoriteFilters")
            execSQL("ALTER TABLE favoriteFilters_1 RENAME TO favoriteFilters")
        }
    }
}
