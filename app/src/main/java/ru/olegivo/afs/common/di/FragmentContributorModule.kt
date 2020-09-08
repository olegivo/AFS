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
import ru.olegivo.afs.main.android.MainFragment
import ru.olegivo.afs.schedule.android.ScheduleDetailsFragment
import ru.olegivo.afs.schedules.android.DayScheduleFragment
import ru.olegivo.afs.schedules.android.WeekScheduleFragment
import ru.olegivo.afs.schedules.di.SchedulesModule
import ru.olegivo.afs.schedules.di.WeekScheduleModule

@Module
interface FragmentContributorModule {

    @PerFragment
    @ContributesAndroidInjector
    fun bindMainFragment(): MainFragment

    @PerFragment
    @ContributesAndroidInjector
    fun bindWeekScheduleFragment(): WeekScheduleFragment

    @PerFragment
    @ContributesAndroidInjector
    fun bindDayScheduleFragment(): DayScheduleFragment

    @PerFragment
    @ContributesAndroidInjector
    fun bindReserveFragment(): ScheduleDetailsFragment

    @PerFragment
    @ContributesAndroidInjector(
        modules = [
            WeekScheduleModule::class,
            SchedulesModule::class,
            FragmentsBindingModule::class
        ]
    )
    fun contributeFragmentFactory(): ScopedFragmentFactory.FragmentProviders
}
