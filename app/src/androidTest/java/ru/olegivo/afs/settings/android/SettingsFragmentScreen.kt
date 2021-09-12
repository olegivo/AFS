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

import com.kaspersky.kaspresso.screens.KScreen
import io.github.kakaocup.kakao.check.KCheckBox
import io.github.kakaocup.kakao.toolbar.KToolbar
import ru.olegivo.afs.R
import ru.olegivo.afs.home.android.HomeFragment

object SettingsFragmentScreen : KScreen<SettingsFragmentScreen>() {
    private val toolbar = KToolbar {
        withId(R.id.toolbar)
    }
    private val isFake = KCheckBox {
        withId(R.id.is_stub_reserve_check_box)
    }

    fun isFakeChecked(expected: Boolean) {
        if (expected) {
            isFake.isChecked()
        } else {
            isFake.isNotChecked()
        }
    }

    fun assertScreenShown() {
        toolbar {
            hasTitle("Settings")
        }
    }

    override val layoutId: Int = R.layout.fragment_settings
    override val viewClass = HomeFragment::class.java
}
