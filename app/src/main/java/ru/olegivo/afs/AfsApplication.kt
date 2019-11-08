package ru.olegivo.afs

import android.app.Application
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import net.danlew.android.joda.JodaTimeAndroid
import ru.olegivo.afs.common.di.DaggerAppComponent
import timber.log.Timber
import javax.inject.Inject

class AfsApplication : Application(), HasAndroidInjector {
    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG_MODE) {
            Timber.plant(Timber.DebugTree())
        }

        JodaTimeAndroid.init(this)
        DaggerAppComponent.factory().create(this).inject(this)
    }

    override fun androidInjector() = androidInjector
}
