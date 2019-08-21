package ru.olegivo.afs.auth.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import ru.olegivo.afs.auth.data.AuthRepositoryImpl
import ru.olegivo.afs.auth.domain.AuthRepository
import javax.inject.Singleton

@Module
abstract class AuthModule {
    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

}
