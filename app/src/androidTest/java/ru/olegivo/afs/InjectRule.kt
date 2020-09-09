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

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.reset
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import ru.olegivo.afs.common.db.AfsDatabase
import ru.olegivo.afs.common.di.DaggerTestAppComponent
import ru.olegivo.afs.common.network.Api
import ru.olegivo.afs.preferences.data.PreferencesDataSource

class InjectRule : TestRule {

    private val mocks: Array<Any> by lazy {
        arrayOf(
            afsDatabase,
            preferencesDataSource,
            api
        )
    }

    val afsDatabase: AfsDatabase = mock()
    val preferencesDataSource: PreferencesDataSource = mock()
    val api: Api = mock()

    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            @Throws(Throwable::class)
            override fun evaluate() {
                inject()
                resetMocks()

                base.evaluate()

                checkNotVerifiedMocks()
            }
        }
    }

    private fun checkNotVerifiedMocks() {
        verifyNoMoreInteractions(*mocks)
    }

    private fun resetMocks() {
        reset(*mocks)
    }

    private fun inject() {
        val afsApplication =
            getAfsApplication()
        afsApplication.testMode = true

        DaggerTestAppComponent.factory()
            .create(
                app = afsApplication,
                api = api,
                preferencesDataSource = preferencesDataSource,
                afsDatabase = afsDatabase
            )
            .inject(afsApplication)
    }
}
