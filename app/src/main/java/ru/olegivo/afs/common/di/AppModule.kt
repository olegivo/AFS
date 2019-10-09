package ru.olegivo.afs.common.di

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.olegivo.afs.AfsApplication
import ru.olegivo.afs.MainActivity
import ru.olegivo.afs.MainFragment
import ru.olegivo.afs.auth.di.AuthModule
import ru.olegivo.afs.clubs.di.ClubsModule
import ru.olegivo.afs.common.domain.DateProvider
import ru.olegivo.afs.common.domain.DateProviderImpl
import ru.olegivo.afs.preferences.di.PreferencesModule
import javax.inject.Named

@Module(
    includes = [
        AppModule.AppProvidesModule::class,
        RxModule::class,
        NavigationModule::class,
        NetworkModule::class,
        AuthModule::class,
        PreferencesModule::class
    ]
)
abstract class AppModule {

    @Binds
    @Named("application")
    abstract fun bindApplicationContext(app: AfsApplication): Context

    @Binds
    abstract fun bindDateProvider(app: DateProviderImpl): DateProvider

    @Module
    class AppProvidesModule
}

@Module
abstract class FragmentBuilderModule {

    @ContributesAndroidInjector(modules = [ClubsModule::class])
    abstract fun bindMainFragment(): MainFragment

}

@Module
abstract class ActivityBuilderModule {

    @ContributesAndroidInjector(modules = [FragmentBuilderModule::class])
    abstract fun bindMainActivity(): MainActivity

}