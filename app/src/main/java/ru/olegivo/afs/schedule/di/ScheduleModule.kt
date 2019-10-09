package ru.olegivo.afs.schedule.di

import dagger.Binds
import dagger.Module
import ru.olegivo.afs.clubs.di.ClubsModule
import ru.olegivo.afs.schedule.data.ScheduleNetworkSource
import ru.olegivo.afs.schedule.data.ScheduleRepositoryImpl
import ru.olegivo.afs.schedule.domain.GetCurrentWeekScheduleUseCase
import ru.olegivo.afs.schedule.domain.GetCurrentWeekScheduleUseCaseImpl
import ru.olegivo.afs.schedule.domain.ScheduleRepository
import ru.olegivo.afs.schedule.network.ScheduleNetworkSourceImpl
import ru.olegivo.afs.schedule.presentation.ScheduleContract
import ru.olegivo.afs.schedule.presentation.WeekSchedulePresenter

@Module(includes = [ClubsModule::class])
abstract class ScheduleModule {
    @Binds
    abstract fun bindSchedulePresenter(impl: WeekSchedulePresenter): ScheduleContract.Presenter

    @Binds
    abstract fun bindGetCurrentWeekScheduleUseCase(impl: GetCurrentWeekScheduleUseCaseImpl): GetCurrentWeekScheduleUseCase

    @Binds
    abstract fun bindScheduleRepository(impl: ScheduleRepositoryImpl): ScheduleRepository

    @Binds
    abstract fun bindScheduleNetworkSource(impl: ScheduleNetworkSourceImpl): ScheduleNetworkSource
}