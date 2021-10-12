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
import ru.olegivo.afs.BuildConfig
import ru.olegivo.afs.settings.android.DatabaseHelperImpl
import ru.olegivo.afs.settings.domain.DatabaseHelper
import ru.olegivo.afs.shared.db.AfsDatabase
import ru.olegivo.afs.shared.favorites.db.FavoriteDao
import ru.olegivo.afs.shared.favorites.db.FavoriteDaoImpl
import ru.olegivo.afs.shared.recordReminders.db.models.RecordReminderSchedules
import ru.olegivo.afs.shared.reserve.db.models.ReservedSchedules
import ru.olegivo.afs.shared.schedules.db.ReserveDao
import ru.olegivo.afs.shared.schedules.db.ReserveDaoImpl
import ru.olegivo.afs.shared.schedules.db.ScheduleDao
import ru.olegivo.afs.shared.schedules.db.ScheduleDaoImpl
import ru.olegivo.afs.shared.schedules.db.models.Schedules
import javax.inject.Named
import javax.inject.Singleton

@Module(includes = [DbModule.BindsModule::class, DbModuleCore::class])
object DbModule {
    @Singleton
    @Provides
    fun providesSqlDriver(@Named("application") context: Context): SqlDriver =
        AndroidSqliteDriver(
            schema = AfsDatabase.Schema,
            context = context,
            name = BuildConfig.DB_NAME
        )

    @Provides
    fun provideFavoriteDao(db: AfsDatabase): FavoriteDao = FavoriteDaoImpl(db)

    @Provides
    fun provideReserveDao(db: AfsDatabase): ReserveDao = ReserveDaoImpl(db)

    @Provides
    fun provideScheduleDao(db: AfsDatabase): ScheduleDao = ScheduleDaoImpl(db)

    @Module
    interface BindsModule {
        @Binds
        fun bindDatabaseHelper(impl: DatabaseHelperImpl): DatabaseHelper
    }
}

@Module
object DbModuleCore {
    private val instantAdapter = object : ColumnAdapter<Instant, Long> {
        override fun decode(databaseValue: Long): Instant =
            Instant.fromEpochMilliseconds(databaseValue)

        override fun encode(value: Instant) = value.toEpochMilliseconds()
    }

    @Singleton
    @Provides
    fun providesAfsDatabaseNew(sqliteDriver: SqlDriver): AfsDatabase {
        return AfsDatabase(
            sqliteDriver,
            recordReminderSchedulesAdapter = RecordReminderSchedules.Adapter(
                dateFromAdapter = instantAdapter,
                dateUntilAdapter = instantAdapter
            ),
            schedulesAdapter = Schedules.Adapter(
                datetimeAdapter = instantAdapter,
                recordFromAdapter = instantAdapter,
                recordToAdapter = instantAdapter
            ),
            reservedSchedulesAdapter = ReservedSchedules.Adapter(
                datetimeAdapter = instantAdapter
            )
        )
    }
}
