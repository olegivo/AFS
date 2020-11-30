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
import ru.olegivo.afs.analytics.data.FirebaseAnalyticsNetworkSource
import ru.olegivo.afs.common.db.AfsDatabase
import ru.olegivo.afs.common.network.Api
import ru.olegivo.afs.preferences.data.PreferencesDataSource

class ExternalDependenciesImpl : ExternalDependencies{
    private val mocks: Array<Any> by lazy {
        arrayOf(
            afsDatabase,
            preferencesDataSource,
            api,
            firebaseAnalyticsNetworkSource
        )
    }

    override val afsDatabase: AfsDatabase = mock()
    override val preferencesDataSource: PreferencesDataSource = mock()
    override val api: Api = mock()
    override val firebaseAnalyticsNetworkSource: FirebaseAnalyticsNetworkSource = mock()

    override fun checkNotVerifiedMocks() {
        verifyNoMoreInteractions(*mocks)
    }

    override fun resetMocks() {
        reset(*mocks)
    }
}
