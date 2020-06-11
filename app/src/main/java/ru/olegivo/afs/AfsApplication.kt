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
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import io.fabric.sdk.android.Fabric
import net.danlew.android.joda.JodaTimeAndroid
import ru.olegivo.afs.common.di.DaggerAppComponent
import ru.olegivo.afs.common.errors.CrashlyticsTree
import ru.olegivo.afs.errors.UncaughtException
import ru.olegivo.afs.schedules.android.ActualizeSchedulesWorker
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Provider

class AfsApplication : Application(), HasAndroidInjector {
    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    @Inject
    lateinit var workManagerProvider: Provider<WorkManager>

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG_MODE) {
            Timber.plant(Timber.DebugTree())
        }

        Timber.plant(CrashlyticsTree())

        UncaughtException.setup()

        val crashlyticsKit = Crashlytics.Builder()
            .core(CrashlyticsCore.Builder()/*.disabled(BuildConfig.DEBUG)*/.build())
            .build()
        Fabric.with(this, crashlyticsKit)

        JodaTimeAndroid.init(this)
        DaggerAppComponent.factory().create(this).let {
            it.inject(this)

            WorkManager.initialize(
                this,
                Configuration.Builder()
                    .setWorkerFactory(it.workerFactory())
                    .build()
            )
        }

        planActualizeSchedulesWork()
    }

    override fun androidInjector() = androidInjector

    private fun planActualizeSchedulesWork() {
        workManagerProvider.get().enqueueUniquePeriodicWork(
            ActualizeSchedulesWorker.TAG,
            ExistingPeriodicWorkPolicy.REPLACE,
            PeriodicWorkRequestBuilder<ActualizeSchedulesWorker>(1, TimeUnit.HOURS) // TODO: periodic settings
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.NOT_ROAMING)
                        .setRequiresBatteryNotLow(true)
                        .build()
                )
                .build()
        )
    }
}
