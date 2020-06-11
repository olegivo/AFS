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
import androidx.work.WorkManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import ru.olegivo.afs.common.android.worker.ChildWorkerFactory
import ru.olegivo.afs.favorites.android.FavoriteRecordReminderWorker
import ru.olegivo.afs.favorites.android.SportsActivityReserveWorker
import ru.olegivo.afs.schedule.di.ScheduleDetailsModule
import ru.olegivo.afs.schedules.android.ActualizeSchedulesWorker
import javax.inject.Named

@Module(
    includes = [
        WorkerBindingModule.ProvidesModule::class,
        ScheduleDetailsModule::class
    ]
)
interface WorkerBindingModule {
    @Binds
    @IntoMap
    @WorkerKey(FavoriteRecordReminderWorker::class)
    fun bindFavoriteRecordReminderWorker(factory: FavoriteRecordReminderWorker.Factory): ChildWorkerFactory

    @Binds
    @IntoMap
    @WorkerKey(SportsActivityReserveWorker::class)
    fun bindSportsActivityReserveWorker(factory: SportsActivityReserveWorker.Factory): ChildWorkerFactory

    @Binds
    @IntoMap
    @WorkerKey(ActualizeSchedulesWorker::class)
    fun bindActualizeSchedulesWorker(factory: ActualizeSchedulesWorker.Factory): ChildWorkerFactory

    @Module
    object ProvidesModule {
        @Provides
        @JvmStatic
        fun provideWorkManager(@Named("application") applicationContext: Context): WorkManager =
            WorkManager.getInstance(applicationContext)
    }
}
