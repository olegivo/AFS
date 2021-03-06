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

package ru.olegivo.afs

import android.app.Application
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.DelegatingWorkerFactory
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.perf.FirebasePerformance
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import ru.olegivo.afs.common.di.DaggerAppComponent
import ru.olegivo.afs.common.errors.CrashlyticsTree
import ru.olegivo.afs.errors.UncaughtException
import ru.olegivo.afs.schedules.android.ActualizeSchedulesWorker
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Provider

class AfsApplication : Application(), HasAndroidInjector, Configuration.Provider {
    internal var testMode = false

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    @Inject
    lateinit var workManagerProvider: Provider<WorkManager>

    @Inject
    lateinit var delegatingWorkerFactory: DelegatingWorkerFactory

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG_MODE) {
            Timber.plant(Timber.DebugTree())
        }

        Timber.plant(CrashlyticsTree())

        UncaughtException.setup()

        FirebaseCrashlytics.getInstance()
            .setCrashlyticsCollectionEnabled(true/*!BuildConfig.DEBUG*/)

        FirebasePerformance.getInstance()
            .isPerformanceCollectionEnabled = true

        if (!testMode) {
            DaggerAppComponent.factory().create(this).inject(this)
            planActualizeSchedulesWork()
        }
    }

    override fun androidInjector() = androidInjector

    private fun planActualizeSchedulesWork() {
        workManagerProvider.get().enqueueUniquePeriodicWork(
            ActualizeSchedulesWorker.TAG,
            ExistingPeriodicWorkPolicy.REPLACE,
            PeriodicWorkRequestBuilder<ActualizeSchedulesWorker>(
                1,
                TimeUnit.HOURS
            ) // TODO: periodic settings
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.NOT_ROAMING)
                        .setRequiresBatteryNotLow(true)
                        .build()
                )
                .build()
        )
    }

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.DEBUG)
            .setWorkerFactory(delegatingWorkerFactory)
            .build()
}
