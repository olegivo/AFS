package ru.olegivo.afs.schedule.di

import dagger.Binds
import dagger.Module
import ru.olegivo.afs.schedule.data.ReserveNetworkSource
import ru.olegivo.afs.schedule.data.ReserveRepositoryImpl
import ru.olegivo.afs.schedule.domain.GetSportsActivityUseCase
import ru.olegivo.afs.schedule.domain.GetSportsActivityUseCaseImpl
import ru.olegivo.afs.schedule.domain.ReserveRepository
import ru.olegivo.afs.schedule.domain.ReserveUseCase
import ru.olegivo.afs.schedule.domain.ReserveUseCaseImpl
import ru.olegivo.afs.schedule.domain.SavedAgreementUseCase
import ru.olegivo.afs.schedule.domain.SavedAgreementUseCaseImpl
import ru.olegivo.afs.schedule.domain.SavedReserveContactsUseCase
import ru.olegivo.afs.schedule.domain.SavedReserveContactsUseCaseImpl
import ru.olegivo.afs.schedule.network.ReserveNetworkSourceImpl
import ru.olegivo.afs.schedule.presentation.ScheduleDetailsContract
import ru.olegivo.afs.schedule.presentation.ScheduleDetailsPresenter
import ru.olegivo.afs.schedules.di.SchedulesModule

@Module(includes = [SchedulesModule::class])
abstract class ScheduleDetailsModule {
    @Binds
    abstract fun bindReservePresenter(impl: ScheduleDetailsPresenter): ScheduleDetailsContract.Presenter

    @Binds
    abstract fun bindGetSportsActivityUseCase(impl: GetSportsActivityUseCaseImpl): GetSportsActivityUseCase

    @Binds
    abstract fun bindReserveUseCase(impl: ReserveUseCaseImpl): ReserveUseCase

    @Binds
    abstract fun bindSavedAgreementUseCase(impl: SavedAgreementUseCaseImpl): SavedAgreementUseCase

    @Binds
    abstract fun bindSavedReserveContactsUseCase(impl: SavedReserveContactsUseCaseImpl): SavedReserveContactsUseCase

    @Binds
    abstract fun bindReserveRepository(impl: ReserveRepositoryImpl): ReserveRepository

    @Binds
    abstract fun bindReserveNetworkSource(impl: ReserveNetworkSourceImpl): ReserveNetworkSource
}