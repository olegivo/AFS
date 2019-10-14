package ru.olegivo.afs.reserve.di

import dagger.Binds
import dagger.Module
import ru.olegivo.afs.reserve.data.ReserveNetworkSource
import ru.olegivo.afs.reserve.data.ReserveRepositoryImpl
import ru.olegivo.afs.reserve.domain.ReserveRepository
import ru.olegivo.afs.reserve.domain.ReserveUseCase
import ru.olegivo.afs.reserve.domain.ReserveUseCaseImpl
import ru.olegivo.afs.reserve.network.ReserveNetworkSourceImpl
import ru.olegivo.afs.reserve.presentation.ReserveContract
import ru.olegivo.afs.reserve.presentation.ReservePresenter
import ru.olegivo.afs.schedule.data.ScheduleNetworkSource
import ru.olegivo.afs.schedule.network.ScheduleNetworkSourceImpl

@Module
abstract class ReserveModule {
    @Binds
    abstract fun bindReservePresenter(impl: ReservePresenter): ReserveContract.Presenter

    @Binds
    abstract fun bindReserveUseCase(impl: ReserveUseCaseImpl): ReserveUseCase

    @Binds
    abstract fun bindReserveRepository(impl: ReserveRepositoryImpl): ReserveRepository

    @Binds
    abstract fun bindReserveNetworkSource(impl: ReserveNetworkSourceImpl): ReserveNetworkSource
}