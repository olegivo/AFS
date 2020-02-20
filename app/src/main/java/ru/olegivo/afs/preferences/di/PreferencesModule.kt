package ru.olegivo.afs.preferences.di

import dagger.Binds
import dagger.Module
import ru.olegivo.afs.preferences.android.PreferencesDataSourceImpl
import ru.olegivo.afs.preferences.data.PreferencesDataSource
import javax.inject.Singleton

@Module
abstract class PreferencesModule {
    @Binds
    @Singleton
    abstract fun bindPreferencesDataSource(impl: PreferencesDataSourceImpl): PreferencesDataSource
}
