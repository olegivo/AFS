package ru.olegivo.afs.common.di

import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import ru.olegivo.afs.AfsApplication
import ru.olegivo.afs.common.android.worker.AppWorkerFactory
import ru.olegivo.afs.common.android.worker.di.WorkerBindingModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        AppAssistedInjectModule::class,
        AppModule::class,
        ActivityBuilderModule::class,
        BroadcastReceiverModule::class,
        WorkerBindingModule::class
    ]
)
interface AppComponent : AndroidInjector<AfsApplication> {

    fun workerFactory(): AppWorkerFactory

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance app: AfsApplication): AppComponent
    }
}
