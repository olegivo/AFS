package ru.olegivo.afs.schedules.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import ru.olegivo.afs.clubs.di.ClubsModule
import ru.olegivo.afs.common.db.AfsDatabase
import ru.olegivo.afs.favorites.di.FavoritesModule
import ru.olegivo.afs.schedules.data.ScheduleDbSource
import ru.olegivo.afs.schedules.data.ScheduleNetworkSource
import ru.olegivo.afs.schedules.data.ScheduleRepositoryImpl
import ru.olegivo.afs.schedules.db.ScheduleDbSourceImpl
import ru.olegivo.afs.schedules.domain.GetCurrentWeekScheduleUseCase
import ru.olegivo.afs.schedules.domain.GetCurrentWeekSportsActivitiesUseCaseImpl
import ru.olegivo.afs.schedules.domain.ScheduleRepository
import ru.olegivo.afs.schedules.network.ScheduleNetworkSourceImpl
import ru.olegivo.afs.schedules.presentation.ScheduleContract
import ru.olegivo.afs.schedules.presentation.WeekSchedulePresenter

@Module(
    includes = [
        ScheduleModule.ProvidesModule::class,
        ClubsModule::class,
        FavoritesModule::class
    ]
)
abstract class ScheduleModule {
    @Binds
    abstract fun bindSchedulePresenter(impl: WeekSchedulePresenter): ScheduleContract.Presenter

    @Binds
    abstract fun bindGetCurrentWeekScheduleUseCase(impl: GetCurrentWeekSportsActivitiesUseCaseImpl): GetCurrentWeekScheduleUseCase

    @Binds
    abstract fun bindScheduleRepository(impl: ScheduleRepositoryImpl): ScheduleRepository

    @Binds
    abstract fun bindScheduleNetworkSource(impl: ScheduleNetworkSourceImpl): ScheduleNetworkSource

    @Binds
    abstract fun bindReserveDbSource(impl: ScheduleDbSourceImpl): ScheduleDbSource

    @Module
    object ProvidesModule {
        @Provides
        fun provideScheduleDao(afsDatabase: AfsDatabase) = afsDatabase.schedule
    }
}