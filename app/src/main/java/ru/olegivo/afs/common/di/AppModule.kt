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

package ru.olegivo.afs.common.di

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.olegivo.afs.AfsApplication
import ru.olegivo.afs.auth.di.AuthModule
import ru.olegivo.afs.clubs.di.ClubsModule
import ru.olegivo.afs.common.android.ErrorReporterImpl
import ru.olegivo.afs.common.domain.DateProvider
import ru.olegivo.afs.common.domain.DateProviderImpl
import ru.olegivo.afs.common.domain.DateTimeUtils
import ru.olegivo.afs.common.domain.DateTimeUtilsImpl
import ru.olegivo.afs.common.domain.ErrorReporter
import ru.olegivo.afs.favorites.android.FavoriteRecordReminderReceiver
import ru.olegivo.afs.favorites.android.SportsActivityReserveReceiver
import ru.olegivo.afs.main.android.MainActivity
import ru.olegivo.afs.main.android.MainFragment
import ru.olegivo.afs.preferences.di.PreferencesModule
import ru.olegivo.afs.schedule.android.ScheduleDetailsFragment
import ru.olegivo.afs.schedule.di.ScheduleDetailsModule
import ru.olegivo.afs.schedules.android.DayScheduleFragment
import ru.olegivo.afs.schedules.android.WeekScheduleFragment
import ru.olegivo.afs.schedules.di.SchedulesModule
import ru.olegivo.afs.schedules.di.WeekScheduleModule
import javax.inject.Named

@Module(
    includes = [
        AppModule.AppProvidesModule::class,
        CoroutinesModule::class,
        RxModule::class,
        NavigationModule::class,
        NetworkModule::class,
        DbModule::class,
        AuthModule::class,
        PreferencesModule::class
    ]
)
abstract class AppModule {

    @Binds
    @Named("application")
    abstract fun bindApplicationContext(app: AfsApplication): Context

    @Binds
    abstract fun bindDateProvider(app: DateProviderImpl): DateProvider

    @Binds
    abstract fun bindDateTimeUtils(app: DateTimeUtilsImpl): DateTimeUtils

    @Binds
    abstract fun bindErrorReporter(app: ErrorReporterImpl): ErrorReporter

    @Module
    class AppProvidesModule
}

@Module
abstract class FragmentBuilderModule {

    @ContributesAndroidInjector(modules = [ClubsModule::class, ScheduleDetailsModule::class])
    abstract fun bindMainFragment(): MainFragment

    @PerFragment
    @ContributesAndroidInjector(modules = [WeekScheduleModule::class])
    abstract fun bindWeekScheduleFragment(): WeekScheduleFragment

    @ContributesAndroidInjector(modules = [SchedulesModule::class])
    abstract fun bindDayScheduleFragment(): DayScheduleFragment

    @ContributesAndroidInjector(modules = [ScheduleDetailsModule::class])
    abstract fun bindReserveFragment(): ScheduleDetailsFragment
}

@Module
abstract class ActivityBuilderModule {

    @ContributesAndroidInjector(modules = [FragmentBuilderModule::class])
    abstract fun bindMainActivity(): MainActivity
}

@Module
abstract class BroadcastReceiverModule {

    @ContributesAndroidInjector
    abstract fun contributesFavoriteRecordReminderReceiver(): FavoriteRecordReminderReceiver

    @ContributesAndroidInjector
    abstract fun contributesSportsActivityReserveReceiver(): SportsActivityReserveReceiver
}
