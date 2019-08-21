package ru.olegivo.afs.common.di

import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import ru.olegivo.afs.AfsApplication
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        AppModule::class
    ]
)
interface AppComponent : AndroidInjector<AfsApplication> {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(app: AfsApplication): Builder

        fun build(): AppComponent
    }
}

