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

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.TestScheduler

interface RxHelper {

    val testScheduler: TestScheduler
    val rxSchedulerRule: RxSchedulerRule
    fun <T> Single<T>.assertResult(block: (T) -> Unit): Unit
    fun <T> Maybe<T>.assertResult(block: (T) -> Unit): Unit
    fun Completable.assertSuccess(): Unit
    fun triggerActions()

    abstract class SchedulerSubstitutionStrategy(protected val substituteAndroidSchedulers: Boolean = true) {
        open fun resetRxSchedulers() {
            if (substituteAndroidSchedulers) {
                RxAndroidPlugins.reset()
            }
            RxJavaPlugins.reset()
        }

        abstract fun substituteRxSchedulers()

        object None : SchedulerSubstitutionStrategy() {
            override fun substituteRxSchedulers() {
            }
        }

        interface TestSchedulerHolder {
            val testScheduler: TestScheduler
        }

        abstract class TheOneSchedulerSubstitution(
            substituteAndroidSchedulers: Boolean,
            private val scheduler: Scheduler
        ) :
            SchedulerSubstitutionStrategy(substituteAndroidSchedulers) {

            override fun substituteRxSchedulers() {
                if (substituteAndroidSchedulers) {
                    RxAndroidPlugins.setInitMainThreadSchedulerHandler { scheduler }
                    RxAndroidPlugins.setMainThreadSchedulerHandler { scheduler }
                }

                RxJavaPlugins.setComputationSchedulerHandler { scheduler }
                RxJavaPlugins.setInitComputationSchedulerHandler { scheduler }

                RxJavaPlugins.setIoSchedulerHandler { scheduler }
                RxJavaPlugins.setInitIoSchedulerHandler { scheduler }

                RxJavaPlugins.setSingleSchedulerHandler { scheduler }
                RxJavaPlugins.setInitSingleSchedulerHandler { scheduler }

                RxJavaPlugins.setNewThreadSchedulerHandler { scheduler }
                RxJavaPlugins.setInitNewThreadSchedulerHandler { scheduler }
            }
        }

        class TestSchedulerEverywhere(
            override val testScheduler: TestScheduler = TestScheduler(),
            substituteAndroidSchedulers: Boolean
        ) : TheOneSchedulerSubstitution(substituteAndroidSchedulers, testScheduler),
            TestSchedulerHolder

        class TrampolineScheduler(substituteAndroidSchedulers: Boolean) :
            TheOneSchedulerSubstitution(substituteAndroidSchedulers, Schedulers.trampoline())
    }
}
