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

import com.nhaarman.mockitokotlin2.mock
import ru.olegivo.afs.analytics.data.FirebaseAnalyticsNetworkSource
import ru.olegivo.afs.common.db.AfsDatabase
import ru.olegivo.afs.common.db.FakeAfsDatabase
import ru.olegivo.afs.common.network.Api
import ru.olegivo.afs.preferences.data.FakePreferencesDataSource
import ru.olegivo.afs.preferences.data.PreferencesDataSource
import ru.olegivo.afs.settings.domain.DatabaseHelper

class ExternalDependenciesImpl(
    strategy: RxHelper.SchedulerSubstitutionStrategy =
        AndroidTestSchedulerStrategies.RxIdlerTestSchedulerStrategy()
    //AndroidTestSchedulerStrategies.RxIdlerStrategy
) :
    ExternalDependencies,
    MocksHolder by MocksHolderImpl(),
    RxHelper by RxHelperImpl(strategy) {

    private val fakePreferencesDataSource = FakePreferencesDataSource()
    private val fakeAfsDatabase = FakeAfsDatabase()

    override val afsDatabase: AfsDatabase = fakeAfsDatabase
    override val preferencesDataSource: PreferencesDataSource = fakePreferencesDataSource
    override val api: Api = mock()
    override val firebaseAnalyticsNetworkSource: FirebaseAnalyticsNetworkSource = mock()
    override val databaseHelper: DatabaseHelper = mock()

    init {
        addMocks(
            api,
            firebaseAnalyticsNetworkSource,
            databaseHelper
        )
    }

    override fun resetFakes() {
        fakePreferencesDataSource.reset()
        fakeAfsDatabase.reset()
    }

    override fun withFakeDatabase(block: FakeAfsDatabase.Actions.() -> Unit) {
        FakeAfsDatabase.Actions(fakeAfsDatabase).apply(block)
    }
}
