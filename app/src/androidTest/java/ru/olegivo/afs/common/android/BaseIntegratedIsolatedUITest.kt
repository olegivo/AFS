/*
 * Copyright (C) 2021 Oleg Ivashchenko <olegivo@gmail.com>
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

package ru.olegivo.afs.common.android

import org.junit.After
import org.junit.Before
import org.junit.Rule
import ru.olegivo.afs.BaseFixture
import ru.olegivo.afs.ExternalDependencies
import ru.olegivo.afs.ExternalDependenciesImpl

abstract class BaseIntegratedIsolatedUITest<TFixture, TScreen>(
    externalDependencies: ExternalDependencies = ExternalDependenciesImpl()
) :
    ExternalDependencies by externalDependencies
    where TFixture : ChainRuleHolder, TFixture : BaseFixture<TScreen> {

    protected val fixture: TFixture by lazy { createFixture(externalDependencies) }

    @get:Rule
    val ruleChain
        get() = fixture.chain

    @Before
    fun setUp() {
        resetMocks()
        resetFakes()
    }

    @After
    fun tearDown() {
        triggerActions()
        checkNotVerifiedMocks()
    }

    protected abstract fun createFixture(externalDependencies: ExternalDependencies): TFixture
}
