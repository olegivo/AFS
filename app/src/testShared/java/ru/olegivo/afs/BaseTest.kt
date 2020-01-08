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