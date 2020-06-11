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

import com.nhaarman.mockitokotlin2.reset
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.observers.BaseTestConsumer
import io.reactivex.schedulers.TestScheduler
import org.junit.After
import org.junit.Before
import org.junit.Rule
import ru.olegivo.afs.helpers.getSingleValue

abstract class BaseTest {
    @Rule
    @JvmField
    val schedulerRule = RxSchedulerRule()
    protected val testScheduler: TestScheduler get() = schedulerRule.testScheduler
    private val mocks: Array<Any> by lazy { getAllMocks() }
    protected abstract fun getAllMocks(): Array<Any>

    @Before
    open fun setUp() {
        if (mocks.isNotEmpty()) reset(*mocks)
        schedulerRule.testScheduler.triggerActions()
    }

    @After
    fun tearDown() {
        if (mocks.isNotEmpty()) verifyNoMoreInteractions(*mocks)
    }

    fun <T> T.andTriggerActions(): T = also {
        schedulerRule.testScheduler.triggerActions()
    }

    protected fun <T> Single<T>.assertResult(block: (T) -> Unit): Unit =
        test().andTriggerActions()
            .assertSuccess {
                getSingleValue().run(block)
            }

    protected fun <T> Maybe<T>.assertResult(block: (T) -> Unit): Unit =
        test().andTriggerActions()
            .assertSuccess {
                getSingleValue().run(block)
            }

    protected fun Completable.assertSuccess(): Unit =
        test().andTriggerActions()
            .assertSuccess { }

    private fun <T, U : BaseTestConsumer<T, U>> BaseTestConsumer<T, U>.assertSuccess(block: U.() -> Unit) =
        this.andTriggerActions()
            .assertNoErrors()
            .assertComplete()
            .run(block)
}
