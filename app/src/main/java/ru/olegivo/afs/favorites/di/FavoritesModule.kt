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
import ru.olegivo.afs.favorites.domain.GetFavoritesUseCase
import ru.olegivo.afs.favorites.domain.GetFavoritesUseCaseImpl
import ru.olegivo.afs.favorites.domain.PlanFavoriteRecordReminderUseCase
import ru.olegivo.afs.favorites.domain.PlanFavoriteRecordReminderUseCaseImpl
import ru.olegivo.afs.favorites.domain.RestoreAllActiveRecordRemindersUseCase
import ru.olegivo.afs.favorites.domain.RestoreAllActiveRecordRemindersUseCaseImpl
import ru.olegivo.afs.favorites.domain.ScheduleReminderNotifier
import ru.olegivo.afs.favorites.domain.ShowRecordReminderUseCase
import ru.olegivo.afs.favorites.domain.ShowRecordReminderUseCaseImpl
import ru.olegivo.afs.favorites.presentation.FavoritesContract
import ru.olegivo.afs.favorites.presentation.FavoritesPresenter
import ru.olegivo.afs.schedule.domain.RemoveFromFavoritesUseCase
import ru.olegivo.afs.schedule.domain.RemoveFromFavoritesUseCaseImpl

@Module(includes = [FavoritesModule.ProvidesModule::class])
interface FavoritesModule {
    @Binds
    fun bindAddToFavoritesUseCase(impl: AddToFavoritesUseCaseImpl): AddToFavoritesUseCase

    @Binds
    fun bindRestoreAllActiveRecordRemindersUseCase(impl: RestoreAllActiveRecordRemindersUseCaseImpl): RestoreAllActiveRecordRemindersUseCase

    @Binds
    fun bindShowRecordReminderUseCase(impl: ShowRecordReminderUseCaseImpl): ShowRecordReminderUseCase

    @Binds
    fun bindPlanFavoriteRecordReminderUseCase(impl: PlanFavoriteRecordReminderUseCaseImpl): PlanFavoriteRecordReminderUseCase

    @Binds
    fun bindRemoveFromFavoritesUseCase(impl: RemoveFromFavoritesUseCaseImpl): RemoveFromFavoritesUseCase

    @Binds
    fun bindFavoritesRepository(impl: FavoritesRepositoryImpl): FavoritesRepository

    @Binds
    fun bindFavoritesDbSource(impl: FavoritesDbSourceImpl): FavoritesDbSource

    @Binds
    fun bindFavoriteAlarmPlanner(impl: FavoriteAlarmPlannerImpl): FavoriteAlarmPlanner

    @Binds
    fun bindScheduleReminderNotifier(impl: ScheduleReminderNotifierImpl): ScheduleReminderNotifier

    @Binds
    fun bindFavoritesPresenter(impl: FavoritesPresenter): FavoritesContract.Presenter

    @Binds
    fun bindGetFavoritesUseCase(impl: GetFavoritesUseCaseImpl): GetFavoritesUseCase

    @Module
    object ProvidesModule {
        @Provides
        fun provideFavoriteDao(afsDatabase: AfsDatabase) = afsDatabase.favorites
    }
}
