package ru.olegivo.afs.common.android.worker.di

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.olegivo.afs.common.android.worker.ChildWorkerFactory
import ru.olegivo.afs.favorites.android.FavoriteRecordReminderWorker
import ru.olegivo.afs.schedule.di.ScheduleDetailsModule

@Module(
    includes = [
        ScheduleDetailsModule::class
    ]
)
interface WorkerBindingModule {
    @Binds
    @IntoMap
    @WorkerKey(FavoriteRecordReminderWorker::class)
    fun bindFavoriteRecordReminderWorker(factory: FavoriteRecordReminderWorker.Factory): ChildWorkerFactory
}
