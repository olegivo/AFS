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

import org.junit.Before
import org.junit.Test
import ru.olegivo.afs.ExternalDependencies
import ru.olegivo.afs.common.android.BaseIntegratedIsolatedUITest
import ru.olegivo.afs.suite.IntegratedIsolatedUITest

@IntegratedIsolatedUITest
class SettingsFragmentTest :
    BaseIntegratedIsolatedUITest<SettingsFragmentFixture, SettingsFragmentScreen>() {

    @Before
    fun setUp() {
    }

    override fun createFixture(externalDependencies: ExternalDependencies) =
        SettingsFragmentFixture(externalDependencies)

    @Test
    fun is_fake_checked_when_has_saved_true() {
        val expected = true
        fixture.prepare(isStubReserveResponse = expected)

        fixture.screen {
            isFakeChecked(expected)
        }
    }

    @Test
    fun is_fake_unchecked_when_has_saved_false() {
        val expected = false
        fixture.prepare(isStubReserveResponse = expected)

        fixture.screen {
            isFakeChecked(expected)
        }
    }
}
