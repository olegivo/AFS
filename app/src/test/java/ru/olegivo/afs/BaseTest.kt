package ru.olegivo.afs

import com.nhaarman.mockitokotlin2.reset
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import org.junit.After
import org.junit.Before
import org.junit.Rule

abstract class BaseTest {
    @Rule
    @JvmField
    val schedulerRule = RxSchedulerRule()
    private val mocks: Array<Any> by lazy { getAllMocks() }
    protected abstract fun getAllMocks(): Array<Any>

    @Before
    fun setUp() {
        if(mocks.isNotEmpty()) reset(*mocks)
        schedulerRule.testScheduler.triggerActions()
    }

    @After
    fun tearDown() {
        if(mocks.isNotEmpty()) verifyNoMoreInteractions(*mocks)
    }

    fun <T> T.andTriggerActions(): T = also {
        schedulerRule.testScheduler.triggerActions()
    }
}