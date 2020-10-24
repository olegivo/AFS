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

import androidx.fragment.app.Fragment
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.olegivo.afs.favorites.android.FavoritesFragment
import ru.olegivo.afs.main.android.MainFragment
import ru.olegivo.afs.schedule.android.ScheduleDetailsFragment
import ru.olegivo.afs.schedules.android.DayScheduleFragment
import ru.olegivo.afs.schedules.android.WeekScheduleFragment

@Module
interface FragmentsBindingModule {

    @Binds
    @IntoMap
    @FragmentKey(MainFragment::class)
    fun bindMainFragment(fragment: MainFragment): Fragment

    @Binds
    @IntoMap
    @FragmentKey(WeekScheduleFragment::class)
    fun bindWeekScheduleFragment(fragment: WeekScheduleFragment): Fragment

    @Binds
    @IntoMap
    @FragmentKey(DayScheduleFragment::class)
    fun bindDayScheduleFragment(fragment: DayScheduleFragment): Fragment

    @Binds
    @IntoMap
    @FragmentKey(FavoritesFragment::class)
    fun bindFavoritesFragment(fragment: FavoritesFragment): Fragment

    @Binds
    @IntoMap
    @FragmentKey(ScheduleDetailsFragment::class)
    fun bindScheduleDetailsFragment(fragment: ScheduleDetailsFragment): Fragment
}
