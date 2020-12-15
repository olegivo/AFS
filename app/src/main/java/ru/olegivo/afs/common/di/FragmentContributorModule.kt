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

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.olegivo.afs.favorites.android.FavoritesFragment
import ru.olegivo.afs.favorites.di.FavoritesModule
import ru.olegivo.afs.home.android.HomeFragment
import ru.olegivo.afs.home.di.HomeModule
import ru.olegivo.afs.schedule.android.ScheduleDetailsFragment
import ru.olegivo.afs.schedules.android.DayScheduleFragment
import ru.olegivo.afs.schedules.android.WeekScheduleFragment
import ru.olegivo.afs.schedules.di.SchedulesModule
import ru.olegivo.afs.schedules.di.WeekScheduleModule
import ru.olegivo.afs.settings.android.SettingsFragment
import ru.olegivo.afs.settings.di.SettingsModule

@Module
interface FragmentContributorModule {

    @PerFragment
    @ContributesAndroidInjector
    fun bindHomeFragment(): HomeFragment

    @PerFragment
    @ContributesAndroidInjector
    fun bindSettingsFragment(): SettingsFragment

    @PerFragment
    @ContributesAndroidInjector
    fun bindWeekScheduleFragment(): WeekScheduleFragment

    @PerFragment
    @ContributesAndroidInjector
    fun bindDayScheduleFragment(): DayScheduleFragment

    @PerFragment
    @ContributesAndroidInjector
    fun bindFavoritesFragment(): FavoritesFragment

    @PerFragment
    @ContributesAndroidInjector
    fun bindReserveFragment(): ScheduleDetailsFragment

    @PerFragment
    @ContributesAndroidInjector(
        modules = [
            HomeModule::class,
            SettingsModule::class,
            WeekScheduleModule::class,
            SchedulesModule::class,
            FavoritesModule::class,
            FragmentsBindingModule::class
        ]
    )
    fun contributeFragmentFactory(): ScopedFragmentFactory.FragmentProviders
}
