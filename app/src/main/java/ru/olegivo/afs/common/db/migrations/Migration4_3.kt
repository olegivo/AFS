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
import ru.olegivo.afs.common.add
import ru.olegivo.afs.common.db.DbVersions
import ru.olegivo.afs.common.db.doInTransaction
import java.util.Date

val migration4_3 = object : Migration(DbVersions.v4, DbVersions.v3) {

    override fun migrate(database: SupportSQLiteDatabase) {
        database.doInTransaction {
            database.execSQL(
                """CREATE TABLE IF NOT EXISTS `favoriteFilters_MERGE_TABLE` (
                |`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                |`groupId` INTEGER NOT NULL, 
                |`group` TEXT NOT NULL, 
                |`activityId` INTEGER NOT NULL, 
                |`activity` TEXT NOT NULL, 
                |`dayOfWeek` INTEGER NOT NULL, 
                |`timeOfDay` INTEGER NOT NULL
                |)""".trimMargin()
            )
            database.execSQL(
                """INSERT INTO `favoriteFilters_MERGE_TABLE` 
|(`id`,`groupId`,`group`,`activityId`,`activity`,`dayOfWeek`,`timeOfDay`) 
|SELECT `id`,`groupId`,`group`,`activityId`,`activity`,`dayOfWeek`,`minutesOfDay`
|FROM `favoriteFilters`""".trimMargin()
            )
            database.query("select `id`, `minutesOfDay` from favoriteFilters").use {
                while (it.moveToNext()) {
                    val id = it.getInt(it.getColumnIndex("id"))
                    val minutesOfDay = it.getInt(it.getColumnIndex("minutesOfDay"))
                    val timeOfDay = Date(0L).add(minutes = minutesOfDay).time
                    database.execSQL("update favoriteFilters_MERGE_TABLE set timeOfDay = $timeOfDay where id = $id")
                }
            }

            database.execSQL("DROP TABLE IF EXISTS `favoriteFilters`")
            database.execSQL("ALTER TABLE `favoriteFilters_MERGE_TABLE` RENAME TO `favoriteFilters`")
        }
    }
}
