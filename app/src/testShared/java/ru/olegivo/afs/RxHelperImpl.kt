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
import io.reactivex.Single
import io.reactivex.observers.BaseTestConsumer
import io.reactivex.schedulers.TestScheduler
import ru.olegivo.afs.helpers.getSingleValue

class RxHelperImpl :
    RxHelper {

    val schedulerRule = RxSchedulerRule()
    override val testScheduler: TestScheduler
        get() = schedulerRule.testScheduler

    override fun <T> Single<T>.assertResult(block: (T) -> Unit): Unit =
        test().andTriggerActions()
            .assertSuccess {
                getSingleValue().run(block)
            }

    override fun <T> Maybe<T>.assertResult(block: (T) -> Unit): Unit =
        test().andTriggerActions()
            .assertSuccess {
                getSingleValue().run(block)
            }

    override fun Completable.assertSuccess(): Unit =
        test().andTriggerActions()
            .assertSuccess { }

    override fun triggerActions() {
        testScheduler.triggerActions()
    }

    private fun <T, U : BaseTestConsumer<T, U>> BaseTestConsumer<T, U>.assertSuccess(block: U.() -> Unit) =
        this.andTriggerActions()
            .assertNoErrors()
            .assertComplete()
            .run(block)

    private fun <T> T.andTriggerActions() = this.also { triggerActions() }
}
