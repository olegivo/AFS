package ru.olegivo.afs

import android.app.Application
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import net.danlew.android.joda.JodaTimeAndroid
import ru.olegivo.afs.common.di.DaggerAppComponent
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

        UncaughtException.setup()

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
