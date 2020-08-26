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
import org.junit.After
import org.junit.Before
import org.junit.Rule

abstract class BaseTest(
    private val rxHelperImpl: RxHelperImpl = RxHelperImpl(),
    private val dispatcherHelperImpl: DispatcherHelperImpl = DispatcherHelperImpl()
) :
    RxHelper by rxHelperImpl,
    DispatcherHelper by dispatcherHelperImpl {

    @Rule
    @JvmField
    val schedulerRule = rxHelperImpl.schedulerRule

    @Rule
    @JvmField
    val dispatcherRule = dispatcherHelperImpl.dispatcherRule

    private val mocks: Array<Any> by lazy { getAllMocks() }
    protected abstract fun getAllMocks(): Array<Any>

    @Before
    open fun setUp() {
        if (mocks.isNotEmpty()) reset(*mocks)
        andTriggerActions()
    }

    @After
    open fun tearDown() {
        if (mocks.isNotEmpty()) verifyNoMoreInteractions(*mocks)
    }

    fun <T> T.andTriggerActions(): T = also {
        triggerActions()
        advanceUntilIdle()
    }
}
