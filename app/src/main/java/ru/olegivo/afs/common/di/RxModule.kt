package ru.olegivo.afs.common.di

import dagger.Module
import dagger.Provides
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Named

@Module
class RxModule {

    @Module
    companion object {
        @JvmStatic
        @Provides
        @Named("io")
        fun provideIoScheduler(): Scheduler = Schedulers.io()

        @JvmStatic
        @Provides
        @Named("main")
        fun provideMainScheduler(): Scheduler = AndroidSchedulers.mainThread()
    }
}
