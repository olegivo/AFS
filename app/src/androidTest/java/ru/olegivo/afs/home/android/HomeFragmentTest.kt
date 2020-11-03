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

import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.verify
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import ru.olegivo.afs.InjectRule
import ru.olegivo.afs.RxIdlerRule
import ru.olegivo.afs.extensions.toMaybe
import ru.olegivo.afs.main.android.MainActivity
import ru.olegivo.afs.schedule.data.ReserveRepositoryImpl

class HomeFragmentTest {

    private val activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)
    private val injectRule = InjectRule()
    private val rxIdlerRule = RxIdlerRule()

    @get:Rule
    val chain = RuleChain.outerRule(injectRule)
        .around(rxIdlerRule)
        .around(activityScenarioRule)!!

    @Test
    fun is_fake_checked_when_has_saved_true() {
        val expected = true
        given { injectRule.preferencesDataSource.getBoolean(ReserveRepositoryImpl.IsStubReserve) }
            .willAnswer {
                expected.toMaybe()
            }
        HomeFragmentScreen {
            isFakeChecked(expected)
        }

        verify(injectRule.preferencesDataSource).getBoolean(ReserveRepositoryImpl.IsStubReserve)
    }

    @Test
    fun is_fake_unchecked_when_has_saved_false() {
        val expected = false
        given { injectRule.preferencesDataSource.getBoolean(ReserveRepositoryImpl.IsStubReserve) }
            .willAnswer {
                expected.toMaybe()
            }
        HomeFragmentScreen {
            isFakeChecked(expected)
        }
        verify(injectRule.preferencesDataSource).getBoolean(ReserveRepositoryImpl.IsStubReserve)
    }
}
