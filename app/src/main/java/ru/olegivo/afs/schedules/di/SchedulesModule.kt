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

package ru.olegivo.afs.schedules.di

import dagger.Binds
import dagger.Module
import ru.olegivo.afs.clubs.di.ClubsModule
import ru.olegivo.afs.favorites.di.FavoritesModule
import ru.olegivo.afs.schedules.data.ScheduleDbSource
import ru.olegivo.afs.schedules.data.ScheduleNetworkSource
import ru.olegivo.afs.schedules.data.ScheduleRepositoryImpl
import ru.olegivo.afs.schedules.db.ScheduleDbSourceImpl
import ru.olegivo.afs.schedules.domain.ActualizeScheduleUseCase
import ru.olegivo.afs.schedules.domain.ActualizeScheduleUseCaseImpl
import ru.olegivo.afs.schedules.domain.GetCurrentWeekScheduleUseCase
import ru.olegivo.afs.schedules.domain.GetCurrentWeekSportsActivitiesUseCaseImpl
import ru.olegivo.afs.schedules.domain.GetDaySportsActivitiesUseCase
import ru.olegivo.afs.schedules.domain.GetDaySportsActivitiesUseCaseImpl
import ru.olegivo.afs.schedules.domain.ScheduleRepository
import ru.olegivo.afs.schedules.network.ScheduleNetworkSourceImpl
import ru.olegivo.afs.schedules.presentation.DayScheduleContract
import ru.olegivo.afs.schedules.presentation.DaySchedulePresenter

@Module(
    includes = [
        ClubsModule::class,
        FavoritesModule::class
    ]
)
interface SchedulesModule {
    @Binds
    fun bindDaySchedulePresenter(impl: DaySchedulePresenter): DayScheduleContract.Presenter

    @Binds
    fun bindGetCurrentWeekScheduleUseCase(impl: GetCurrentWeekSportsActivitiesUseCaseImpl): GetCurrentWeekScheduleUseCase

    @Binds
    fun bindGetDaySportsActivitiesUseCase(impl: GetDaySportsActivitiesUseCaseImpl): GetDaySportsActivitiesUseCase

    @Binds
    fun bindActualizeScheduleUseCase(impl: ActualizeScheduleUseCaseImpl): ActualizeScheduleUseCase

    @Binds
    fun bindScheduleRepository(impl: ScheduleRepositoryImpl): ScheduleRepository

    @Binds
    fun bindScheduleNetworkSource(impl: ScheduleNetworkSourceImpl): ScheduleNetworkSource

    @Binds
    fun bindReserveDbSource(impl: ScheduleDbSourceImpl): ScheduleDbSource
}
