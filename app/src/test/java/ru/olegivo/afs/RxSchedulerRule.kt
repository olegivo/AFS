package ru.olegivo.afs

import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.TestScheduler
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class RxSchedulerRule : TestRule {

    val testScheduler = TestScheduler()

    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            @Throws(Throwable::class)
            override fun evaluate() {
                RxAndroidPlugins.reset()

                RxAndroidPlugins.setInitMainThreadSchedulerHandler { testScheduler }
                RxAndroidPlugins.setMainThreadSchedulerHandler { testScheduler }

                RxJavaPlugins.reset()

                RxJavaPlugins.setComputationSchedulerHandler { scheduler -> testScheduler }
                RxJavaPlugins.setInitComputationSchedulerHandler { scheduler -> testScheduler }

                RxJavaPlugins.setIoSchedulerHandler { scheduler -> testScheduler }
                RxJavaPlugins.setInitIoSchedulerHandler { scheduler -> testScheduler }

                RxJavaPlugins.setSingleSchedulerHandler { scheduler -> testScheduler }
                RxJavaPlugins.setInitSingleSchedulerHandler { scheduler -> testScheduler }

                RxJavaPlugins.setNewThreadSchedulerHandler { scheduler -> testScheduler }
                RxJavaPlugins.setInitNewThreadSchedulerHandler { scheduler -> testScheduler }

                base.evaluate()

                RxAndroidPlugins.reset()
                RxJavaPlugins.reset()
            }
        }
    }
}