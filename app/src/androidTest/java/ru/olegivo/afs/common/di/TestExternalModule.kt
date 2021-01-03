/*
 * Copyright (C) 2021 Oleg Ivashchenko <olegivo@gmail.com>
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

package ru.olegivo.afs.common.di

import dagger.Module
import dagger.Provides
import ru.olegivo.afs.analytics.di.AnalyticsCoreModule
import ru.olegivo.afs.common.db.AfsDatabase

@Module(
    includes = [
        TestExternalModule.ProvidesModule::class,
        NetworkModule.ProvidesKtorModule::class,
        AnalyticsCoreModule::class
    ]
)
interface TestExternalModule {
    @Module
    object ProvidesModule {
        @Provides
        fun providesFavoritesDao(afsDatabase: AfsDatabase) = afsDatabase.favorites

        @Provides
        fun providesReserveDao(afsDatabase: AfsDatabase) = afsDatabase.reserve

        @Provides
        fun providesSchedulesDao(afsDatabase: AfsDatabase) = afsDatabase.schedules
    }
}
