package ru.olegivo.afs.schedules.di

import dagger.Binds
import dagger.Module
import ru.olegivo.afs.schedules.presentation.WeekScheduleContract
import ru.olegivo.afs.schedules.presentation.WeekSchedulePresenter

@Module(
    /*includes = [
        SchedulesModule.ProvidesModule::class,
        ClubsModule::class,
        FavoritesModule::class
    ]*/
)
abstract class WeekScheduleModule {
    @Binds
    @ScheduleScope
    abstract fun bindWeekSchedulePresenter(impl: WeekSchedulePresenter): WeekScheduleContract.Presenter
}
