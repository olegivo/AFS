/*
 * Copyright (C) 2021 Oleg Ivashchenko <olegivo@gmail.com>
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

package ru.olegivo.afs.common.di

import android.content.Context
import com.squareup.sqldelight.ColumnAdapter
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import dagger.Binds
import dagger.Module
import dagger.Provides
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import ru.olegivo.afs.BuildConfig
import ru.olegivo.afs.common.db.AfsDatabaseNew
import ru.olegivo.afs.common.toDate
import ru.olegivo.afs.favorites.db.FavoriteDaoImpl
import ru.olegivo.afs.recordReminders.db.models.RecordReminderSchedules
import ru.olegivo.afs.reserve.db.models.ReservedSchedules
import ru.olegivo.afs.schedules.db.ReserveDaoImpl
import ru.olegivo.afs.schedules.db.ScheduleDaoImpl
import ru.olegivo.afs.schedules.db.models.Schedules
import ru.olegivo.afs.settings.android.DatabaseHelperImpl
import ru.olegivo.afs.settings.domain.DatabaseHelper
import ru.olegivo.afs.shared.datetime.ADate
import ru.olegivo.afs.shared.favorites.db.FavoriteDao
import ru.olegivo.afs.shared.schedules.db.ReserveDao
import ru.olegivo.afs.shared.schedules.db.ScheduleDao
import javax.inject.Named
import javax.inject.Singleton

@Module(includes = [DbModule.BindsModule::class, DbModuleCore::class])
object DbModule {
    @Singleton
    @Provides
    fun providesSqlDriver(@Named("application") context: Context): SqlDriver =
        AndroidSqliteDriver(
            schema = AfsDatabaseNew.Schema,
            context = context,
            name = BuildConfig.DB_NAME
        )

    @Module
    interface BindsModule {
        @Binds
        fun bindDatabaseHelper(impl: DatabaseHelperImpl): DatabaseHelper

        @Binds
        fun bindReserveDao(impl: ReserveDaoImpl): ReserveDao

        @Binds
        fun bindScheduleDao(impl: ScheduleDaoImpl): ScheduleDao

        @Binds
        fun bindFavoriteDao(impl: FavoriteDaoImpl): FavoriteDao
    }
}

@Module
object DbModuleCore {
    private val aDateAdapter = object : ColumnAdapter<ADate, Long> {
        override fun decode(databaseValue: Long): ADate =
            TimeZone.currentSystemDefault().let {
                with(it) {
                    ADate(Instant.fromEpochMilliseconds(databaseValue).toLocalDateTime(), it)
                }
            }

        override fun encode(value: ADate) = value.toDate().time
    }

    @Singleton
    @Provides
    fun providesAfsDatabaseNew(sqliteDriver: SqlDriver): AfsDatabaseNew {
        return AfsDatabaseNew(
            sqliteDriver,
            recordReminderSchedulesAdapter = RecordReminderSchedules.Adapter(
                dateFromAdapter = aDateAdapter,
                dateUntilAdapter = aDateAdapter
            ),
            schedulesAdapter = Schedules.Adapter(
                datetimeAdapter = aDateAdapter,
                recordFromAdapter = aDateAdapter,
                recordToAdapter = aDateAdapter
            ),
            reservedSchedulesAdapter = ReservedSchedules.Adapter(
                datetimeAdapter = aDateAdapter
            )
        )
    }
}
