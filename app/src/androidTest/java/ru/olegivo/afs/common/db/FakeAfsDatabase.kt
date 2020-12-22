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

package ru.olegivo.afs.common.db

import androidx.room.DatabaseConfiguration
import androidx.room.InvalidationTracker
import androidx.sqlite.db.SupportSQLiteOpenHelper
import io.reactivex.Completable
import ru.olegivo.afs.favorites.db.FakeFavoriteDao
import ru.olegivo.afs.favorites.db.FavoriteDao
import ru.olegivo.afs.favorites.db.models.FavoriteFilterEntity
import ru.olegivo.afs.schedules.db.FakeReserveDao
import ru.olegivo.afs.schedules.db.FakeScheduleDao
import ru.olegivo.afs.schedules.db.ReserveDao
import ru.olegivo.afs.schedules.db.ScheduleDao
import ru.olegivo.afs.schedules.db.models.ReservedSchedule
import ru.olegivo.afs.schedules.db.models.ScheduleEntity
import java.util.HashMap

class FakeAfsDatabase : AfsDatabase() {
    private val tables = Tables()

    private val fakeScheduleDao = FakeScheduleDao(tables)
    private val fakeFavoriteDao = FakeFavoriteDao(tables)
    private val fakeReserveDao = FakeReserveDao(tables)

    override val schedules: ScheduleDao = fakeScheduleDao
    override val favorites: FavoriteDao = fakeFavoriteDao
    override val reserve: ReserveDao = fakeReserveDao

    override fun createOpenHelper(config: DatabaseConfiguration?): SupportSQLiteOpenHelper {
        TODO("Not yet implemented")
    }

    override fun createInvalidationTracker(): InvalidationTracker {
        // copied from generated AfsDatabase_Impl
        val shadowTablesMap = HashMap<String, String>(0)
        val viewTables = HashMap<String, Set<String>>(0)
        return InvalidationTracker(
            this,
            shadowTablesMap,
            viewTables,
            "schedules",
            "reservedSchedules",
            "favoriteFilters",
            "recordReminderSchedules"
        )
    }

    override fun clearAllTables() {
        TODO("Not yet implemented")
    }

    fun reset() {
        with(tables) {
            schedules.clear()
            favoriteFilters.clear()
            reservedSchedules.clear()
        }
    }

    class Tables {
        val schedules: MutableMap<Long, ScheduleEntity> = mutableMapOf()
        val favoriteFilters: MutableMap<Int, FavoriteFilterEntity> = mutableMapOf()
        val reservedSchedules: MutableMap<Long, ReservedSchedule> = mutableMapOf()
    }

    class Actions(private val fakeAfsDatabase: FakeAfsDatabase) {
        fun <R> action(block: FakeAfsDatabase.() -> R) {
            (fakeAfsDatabase.let(block) as? Completable)?.subscribe()
        }
    }
}
