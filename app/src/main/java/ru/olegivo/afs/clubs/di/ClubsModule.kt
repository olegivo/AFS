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

package ru.olegivo.afs.clubs.di

import dagger.Binds
import dagger.Module
import ru.olegivo.afs.clubs.data.ClubsNetworkSource
import ru.olegivo.afs.clubs.data.ClubsRepositoryImpl
import ru.olegivo.afs.clubs.domain.ClubsRepository
import ru.olegivo.afs.clubs.domain.GetClubsUseCase
import ru.olegivo.afs.clubs.domain.GetClubsUseCaseImpl
import ru.olegivo.afs.clubs.domain.GetCurrentClubUseCase
import ru.olegivo.afs.clubs.domain.GetCurrentClubUseCaseImpl
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
    abstract fun bindGetCurrentClubUseCase(impl: GetCurrentClubUseCaseImpl): GetCurrentClubUseCase

    @Binds
    abstract fun bindClubsRepository(impl: ClubsRepositoryImpl): ClubsRepository

    @Binds
    abstract fun bindClubsNetworkSource(impl: ClubsNetworkSourceImpl): ClubsNetworkSource
}
