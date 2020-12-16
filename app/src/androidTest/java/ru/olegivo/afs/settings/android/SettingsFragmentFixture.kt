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

package ru.olegivo.afs.settings.android

import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.verify
import ru.olegivo.afs.ExternalDependencies
import ru.olegivo.afs.common.android.ChainRuleHolder
import ru.olegivo.afs.extensions.toMaybe
import ru.olegivo.afs.home.android.HomeFragmentFixture
import ru.olegivo.afs.home.android.HomeFragmentScreen
import ru.olegivo.afs.schedule.data.ReserveRepositoryImpl

class SettingsFragmentFixture(
    externalDependencies: ExternalDependencies,
    private val homeFragmentFixture: HomeFragmentFixture = HomeFragmentFixture(externalDependencies)
) : ChainRuleHolder by homeFragmentFixture, ExternalDependencies by externalDependencies {
    fun prepare(isStubReserveResponse: Boolean) {
        prepareStubReserveResponse(isStubReserveResponse)

        HomeFragmentScreen {
            clickSettingsButton()
        }
    }

    fun prepareStubReserveResponse(isStubReserveResponse: Boolean) {
        given { preferencesDataSource.getBoolean(ReserveRepositoryImpl.IsStubReserve) }
            .willAnswer {
                isStubReserveResponse.toMaybe()
            }
    }

    fun checkStubReserve() {
        verify(preferencesDataSource).getBoolean(ReserveRepositoryImpl.IsStubReserve)
    }
}
