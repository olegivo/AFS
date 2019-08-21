package ru.olegivo.afs.common.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.olegivo.afs.MainActivity

@Module(
    includes = [
        AppModule.AppProvidesModule::class
    ]
)
abstract class AppModule {

    @ContributesAndroidInjector
    abstract fun contributeMainActivity(): MainActivity

    @Module
    class AppProvidesModule
}