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

import androidx.test.espresso.IdlingRegistry
import com.squareup.rx2.idler.Rx2Idler
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.TestScheduler

object AndroidTestSchedulerStrategies {

    object RxIdlerStrategy : RxHelper.SchedulerSubstitutionStrategy() {
        override fun substituteRxSchedulers() {
            RxJavaPlugins.setInitComputationSchedulerHandler(
                Rx2Idler.create("RxJava 2.x Computation Scheduler")
            )
            RxJavaPlugins.setInitIoSchedulerHandler(
                Rx2Idler.create("RxJava 2.x IO Scheduler")
            )
            RxAndroidPlugins.setInitMainThreadSchedulerHandler(
                Rx2Idler.create("RxJava 2.x Main Scheduler")
            )
        }
    }

    class RxIdlerTestSchedulerStrategy(override val testScheduler: TestScheduler = TestScheduler()) :
        RxHelper.SchedulerSubstitutionStrategy(),
        RxHelper.SchedulerSubstitutionStrategy.TestSchedulerHolder {

        private val wrapped = Rx2Idler.wrap(testScheduler, "RxJava 2.x Test Scheduler")

        override fun substituteRxSchedulers() {
            IdlingRegistry.getInstance().register(wrapped)
        }

        override fun resetRxSchedulers() {
            super.resetRxSchedulers()
            IdlingRegistry.getInstance().unregister(wrapped)
        }
    }
}
