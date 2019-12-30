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
