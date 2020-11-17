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
import ru.olegivo.afs.common.get
import ru.olegivo.afs.common.getMinutesOfDay
import java.util.Calendar
import java.util.Date
import kotlin.math.abs

val migration4_5 = object : Migration(DbVersions.v4, DbVersions.v5) {

    override fun migrate(database: SupportSQLiteDatabase) {
        database.doInTransaction {
            execSQL("ALTER TABLE `favoriteFilters` ADD `clubId` INTEGER NOT NULL DEFAULT 0")
            val selectFavorites =
                "SELECT `id`,`groupId`,`activityId`,`dayOfWeek`,`minutesOfDay` FROM favoriteFilters"
            val selectSchedules =
                "SELECT `clubId`,`datetime` FROM schedules WHERE groupId = ? AND activityId = ?"
            val now = MigrationsEnvironment.predefinedDate ?: Date()

            query(selectFavorites).use { favoritesCursor ->
                while (favoritesCursor.moveToNext()) {
                    val id = favoritesCursor.getInt("id")
                    val groupId = favoritesCursor.getInt("groupId")
                    val activityId = favoritesCursor.getInt("activityId")
                    val dayOfWeek = favoritesCursor.getInt("dayOfWeek")
                    val minutesOfDay = favoritesCursor.getInt("minutesOfDay")

                    val datesByClub = mutableMapOf<Int, List<Long>>()
                    query(selectSchedules, arrayOf(groupId, activityId)).use { schedulesCursor ->
                        while (schedulesCursor.moveToNext()) {
                            val clubId = schedulesCursor.getInt("clubId")
                            val datetime = schedulesCursor.getLong("datetime")
                            datesByClub.merge(
                                clubId,
                                listOf(datetime)
                            ) { list1, list2 -> list1 + list2 }
                        }
                        processDatesByClub(datesByClub, id) { it ->
                            val filteredByFavorites = it.mapValues { entry ->
                                entry.value
                                    .map { timestamp -> Date(timestamp) }
                                    .filter { date ->
                                        date.get(Calendar.DAY_OF_WEEK) == dayOfWeek && date.getMinutesOfDay() == minutesOfDay
                                    }
                            }.filter { it.value.isNotEmpty() }

                            processDatesByClub(
                                filteredByFavorites,
                                id
                            ) { filtered ->
                                val theClosestToNowClubId = filtered.minByOrNull { entry ->
                                    entry.value.minByOrNull { date -> abs(date.time - now.time) }!!
                                }!!.key

                                processDatesByClub(
                                    mapOf(theClosestToNowClubId to Unit),
                                    id
                                ) {}
                            }
                        }
                    }
                }
            }
        }
    }

    private fun <TValue> SupportSQLiteDatabase.processDatesByClub(
        datesByClub: Map<Int, TValue>,
        id: Int,
        whenManyKeys: (datesByClub: Map<Int, TValue>) -> Unit
    ) {
        when (datesByClub.keys.size) {
            0 -> {
                execSQL(
                    "DELETE FROM favoriteFilters where id = ?",
                    arrayOf(id)
                )
            }
            1 -> {
                execSQL(
                    "UPDATE favoriteFilters SET clubId = ? where id = ?",
                    arrayOf(datesByClub.keys.single(), id)
                )
            }
            else -> {
                whenManyKeys(datesByClub)
            }
        }
    }
}
