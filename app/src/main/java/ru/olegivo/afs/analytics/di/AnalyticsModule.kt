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

package ru.olegivo.afs.analytics.di

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.Binds
import dagger.Module
import dagger.Provides
import ru.olegivo.afs.analytics.data.FirebaseAnalyticsNetworkSource
import ru.olegivo.afs.analytics.data.FirebaseAnalyticsRepository
import ru.olegivo.afs.analytics.domain.AnalyticsProvider
import ru.olegivo.afs.analytics.domain.AnalyticsProviderImpl
import ru.olegivo.afs.analytics.domain.AnalyticsRepository
import ru.olegivo.afs.analytics.domain.EventsFactory
import ru.olegivo.afs.analytics.domain.FirebaseEventsFactory
import ru.olegivo.afs.analytics.network.FirebaseAnalyticsNetworkSourceImpl
import javax.inject.Named
import javax.inject.Singleton

@Module(
    includes = [
        AnalyticsCoreModule::class,
        AnalyticsModule.ProvidesModule::class
    ]
)
interface AnalyticsModule {
    @Binds
    fun bindFirebaseAnalyticsNetworkSource(impl: FirebaseAnalyticsNetworkSourceImpl): FirebaseAnalyticsNetworkSource

    @Module
    object ProvidesModule {
        @Provides
        fun getFirebaseAnalytics(@Named("application") context: Context) = FirebaseAnalytics.getInstance(context)
    }
}

@Module
interface AnalyticsCoreModule {
    @Singleton
    @Binds
    fun bindAnalyticsProvider(impl: AnalyticsProviderImpl): AnalyticsProvider

    @Binds
    fun bindEventsFactory(impl: FirebaseEventsFactory): EventsFactory

    @Binds
    fun bindAnalyticsRepository(impl: FirebaseAnalyticsRepository): AnalyticsRepository
}
