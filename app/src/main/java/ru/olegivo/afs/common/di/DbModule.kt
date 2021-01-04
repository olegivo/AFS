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
import androidx.room.Room
import androidx.room.RoomDatabase
import dagger.Binds
import dagger.Module
import dagger.Provides
import ru.olegivo.afs.BuildConfig
import ru.olegivo.afs.common.db.AfsDatabase
import ru.olegivo.afs.common.db.DbVersions
import ru.olegivo.afs.settings.android.DatabaseHelperImpl
import ru.olegivo.afs.settings.domain.DatabaseHelper
import javax.inject.Named
import javax.inject.Singleton

@Module(includes = [DbModule.BindsModule::class])
object DbModule {
    @Singleton
    @Provides
    @Suppress("SpreadOperator")
    fun providesAfsDatabase(@Named("application") context: Context): AfsDatabase {
        return Room
            .databaseBuilder(context, AfsDatabase::class.java, BuildConfig.DB_NAME)
            .addMigrations(*DbVersions.migrations)
            .setJournalMode(RoomDatabase.JournalMode.WRITE_AHEAD_LOGGING)
            .build()
    }

    @Module
    interface BindsModule {
        @Binds
        fun bindDatabaseHelper(impl: DatabaseHelperImpl): DatabaseHelper
    }
}
