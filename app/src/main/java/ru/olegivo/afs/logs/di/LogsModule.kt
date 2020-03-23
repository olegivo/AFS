package ru.olegivo.afs.logs.di

import dagger.Module
import ru.olegivo.afs.logs.android.AutomaticTagResolver
import ru.olegivo.afs.logs.android.LoggerImpl
import ru.olegivo.afs.logs.domain.Logger

@Module
abstract class LogsModule {
    fun provideLogger(): Logger = LoggerImpl(AutomaticTagResolver())
}