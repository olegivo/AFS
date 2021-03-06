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

package ru.olegivo.afs.common.android.worker.di

import android.content.Context
import androidx.work.DelegatingWorkerFactory
import androidx.work.WorkManager
import androidx.work.WorkerFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet
import ru.olegivo.afs.favorites.android.FavoriteRecordReminderWorker
import ru.olegivo.afs.favorites.android.SportsActivityReserveWorker
import ru.olegivo.afs.schedule.di.ScheduleDetailsModule
import ru.olegivo.afs.schedules.android.ActualizeSchedulesWorker
import javax.inject.Named
import javax.inject.Provider

@Module(
    includes = [
        WorkerBindingModule.ProvidesModule::class,
        ScheduleDetailsModule::class
    ]
)
interface WorkerBindingModule {
    @Binds
    @IntoSet
    fun bindFavoriteRecordReminderWorker(factory: FavoriteRecordReminderWorker.Factory): WorkerFactory

    @Binds
    @IntoSet
    fun bindSportsActivityReserveWorker(factory: SportsActivityReserveWorker.Factory): WorkerFactory

    @Binds
    @IntoSet
    fun bindActualizeSchedulesWorker(factory: ActualizeSchedulesWorker.Factory): WorkerFactory

    @Module
    object ProvidesModule {
        @Provides
        fun provideWorkManager(@Named("application") applicationContext: Context): WorkManager =
            WorkManager.getInstance(applicationContext)

        @Provides
        fun providesDelegatingWorkerFactory(
            childFactories: Provider<Set<WorkerFactory>>
        ): DelegatingWorkerFactory {
            val delegatingWorkerFactory = DelegatingWorkerFactory()
            childFactories.get().forEach {
                delegatingWorkerFactory.addFactory(it)
            }
            return delegatingWorkerFactory
        }
    }
}
