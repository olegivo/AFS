package ru.olegivo.afs.favorites.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import ru.olegivo.afs.common.db.AfsDatabase
import ru.olegivo.afs.favorites.android.FavoriteAlarmPlannerImpl
import ru.olegivo.afs.favorites.android.ScheduleReminderNotifierImpl
import ru.olegivo.afs.favorites.data.FavoritesDbSource
import ru.olegivo.afs.favorites.data.FavoritesRepositoryImpl
import ru.olegivo.afs.favorites.db.FavoritesDbSourceImpl
import ru.olegivo.afs.favorites.domain.AddToFavoritesUseCase
import ru.olegivo.afs.favorites.domain.AddToFavoritesUseCaseImpl
import ru.olegivo.afs.favorites.domain.FavoriteAlarmPlanner
import ru.olegivo.afs.favorites.domain.FavoritesRepository
import ru.olegivo.afs.favorites.domain.PlanFavoriteRecordReminderUseCase
import ru.olegivo.afs.favorites.domain.PlanFavoriteRecordReminderUseCaseImpl
import ru.olegivo.afs.favorites.domain.RestoreAllActiveRecordRemindersUseCase
import ru.olegivo.afs.favorites.domain.RestoreAllActiveRecordRemindersUseCaseImpl
import ru.olegivo.afs.favorites.domain.ScheduleReminderNotifier
import ru.olegivo.afs.favorites.domain.ShowRecordReminderUseCase
import ru.olegivo.afs.favorites.domain.ShowRecordReminderUseCaseImpl
import ru.olegivo.afs.schedule.domain.RemoveFromFavoritesUseCase
import ru.olegivo.afs.schedule.domain.RemoveFromFavoritesUseCaseImpl

@Module(includes = [FavoritesModule.ProvidesModule::class])
abstract class FavoritesModule {
    @Binds
    abstract fun bindAddToFavoritesUseCase(impl: AddToFavoritesUseCaseImpl): AddToFavoritesUseCase

    @Binds
    abstract fun bindRestoreAllActiveRecordRemindersUseCase(impl: RestoreAllActiveRecordRemindersUseCaseImpl): RestoreAllActiveRecordRemindersUseCase

    @Binds
    abstract fun bindShowRecordReminderUseCase(impl: ShowRecordReminderUseCaseImpl): ShowRecordReminderUseCase

    @Binds
    abstract fun bindPlanFavoriteRecordReminderUseCase(impl: PlanFavoriteRecordReminderUseCaseImpl): PlanFavoriteRecordReminderUseCase

    @Binds
    abstract fun bindRemoveFromFavoritesUseCase(impl: RemoveFromFavoritesUseCaseImpl): RemoveFromFavoritesUseCase

    @Binds
    abstract fun bindFavoritesRepository(impl: FavoritesRepositoryImpl): FavoritesRepository

    @Binds
    abstract fun bindFavoritesDbSource(impl: FavoritesDbSourceImpl): FavoritesDbSource

    @Binds
    abstract fun bindFavoriteAlarmPlanner(impl: FavoriteAlarmPlannerImpl): FavoriteAlarmPlanner

    @Binds
    abstract fun bindScheduleReminderNotifier(impl: ScheduleReminderNotifierImpl): ScheduleReminderNotifier

    @Module
    object ProvidesModule {
        @Provides
        fun provideFavoriteDao(afsDatabase: AfsDatabase) = afsDatabase.favorites
    }
}
