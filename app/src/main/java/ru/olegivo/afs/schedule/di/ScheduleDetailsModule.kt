/*
 * Copyright (C) 2020 Oleg Ivashchenko <olegivo@gmail.com>
 *
 * This file is part of AFS.
 *
 * AFS is free software: you can redistribute it and/or modify
 * it under the terms of the MIT License.
 *
 * AFS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * AFS.
 */

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
