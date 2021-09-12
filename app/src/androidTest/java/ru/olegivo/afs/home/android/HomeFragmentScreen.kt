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

import com.kaspersky.kaspresso.screens.KScreen
import io.github.kakaocup.kakao.text.KButton
import ru.olegivo.afs.R

object HomeFragmentScreen : KScreen<HomeFragmentScreen>() {
    private val settingsButton = KButton {
        withId(R.id.settings_button)
    }
    private val favoritesButton = KButton {
        withId(R.id.favorites_button)
    }

    fun clickSettingsButton() {
        settingsButton {
            click()
        }
    }

    fun clickFavoritesButton() {
        favoritesButton {
            click()
        }
    }

    fun assertSettingsButtonShown() {
        settingsButton { isVisible() }
    }

    fun assertFavoritesButtonShown() {
        favoritesButton { isVisible() }
    }

    override val layoutId: Int = R.layout.fragment_home
    override val viewClass = HomeFragment::class.java
}
