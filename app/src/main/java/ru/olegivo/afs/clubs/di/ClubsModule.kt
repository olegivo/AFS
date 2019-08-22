package ru.olegivo.afs.clubs.di

import dagger.Binds
import dagger.Module
import ru.olegivo.afs.clubs.data.ClubsNetworkSource
import ru.olegivo.afs.clubs.data.ClubsRepositoryImpl
import ru.olegivo.afs.clubs.domain.ClubsRepository
import ru.olegivo.afs.clubs.domain.GetClubsUseCase
import ru.olegivo.afs.clubs.domain.GetClubsUseCaseImpl
import ru.olegivo.afs.clubs.domain.SetCurrentClubUseCase
import ru.olegivo.afs.clubs.domain.SetCurrentClubUseCaseImpl
import ru.olegivo.afs.clubs.network.ClubsNetworkSourceImpl

@Module
abstract class ClubsModule {
    @Binds
    abstract fun bindGetClubsUseCase(impl: GetClubsUseCaseImpl): GetClubsUseCase

    @Binds
    abstract fun bindSetCurrentClubUseCase(impl: SetCurrentClubUseCaseImpl): SetCurrentClubUseCase

    @Binds
    abstract fun bindClubsRepository(impl: ClubsRepositoryImpl): ClubsRepository

    @Binds
    abstract fun bindClubsNetworkSource(impl: ClubsNetworkSourceImpl): ClubsNetworkSource
}