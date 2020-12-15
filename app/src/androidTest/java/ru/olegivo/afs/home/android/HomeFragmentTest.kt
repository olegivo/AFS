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

package ru.olegivo.afs.home.android

import org.junit.Test
import ru.olegivo.afs.common.android.BaseIntegratedIsolatedUITest
import ru.olegivo.afs.helpers.getRandomBoolean
import ru.olegivo.afs.settings.android.SettingsFragmentFixture
import ru.olegivo.afs.settings.android.SettingsFragmentScreen
import ru.olegivo.afs.suite.IntegratedIsolatedUITest

@IntegratedIsolatedUITest
class HomeFragmentTest : BaseIntegratedIsolatedUITest<HomeFragmentFixture>() {

    private val settingsFragmentFixture =
        SettingsFragmentFixture(this, homeFragmentFixture = fixture)

    override fun createFixture() = HomeFragmentFixture(this)

    @Test
    fun settings_button_navigates_to_settings_screen() {
        settingsFragmentFixture.prepareStubReserveResponse(getRandomBoolean())
        HomeFragmentScreen {
            clickSettingsButton()
        }
        SettingsFragmentScreen {
            assertScreenShown()
        }
        settingsFragmentFixture.checkStubReserve()
    }
}
