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

package ru.olegivo.afs

import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import ru.olegivo.afs.common.di.DaggerTestAppComponent

class InjectRule(externalDependencies: ExternalDependencies) :
    TestRule,
    ExternalDependencies by externalDependencies {

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

    private fun inject() {
        val afsApplication =
            getAfsApplication()
        afsApplication.testMode = true

        DaggerTestAppComponent.factory()
            .create(
                app = afsApplication,
                api = api,
                preferencesDataSource = preferencesDataSource,
                afsDatabase = afsDatabase,
                firebaseAnalyticsNetworkSource = firebaseAnalyticsNetworkSource,
                databaseHelper = databaseHelper
            )
            .inject(afsApplication)
    }
}
