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
import org.junit.rules.RuleChain
import ru.olegivo.afs.ExternalDependencies
import ru.olegivo.afs.InjectRule
import ru.olegivo.afs.RxIdlerRule
import ru.olegivo.afs.common.android.ChainRueHolder
import ru.olegivo.afs.main.android.MainActivity

class HomeFragmentFixture(externalDependencies: ExternalDependencies) : ChainRueHolder {
    private val activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)
    private val injectRule = InjectRule(externalDependencies)
    private val rxIdlerRule = RxIdlerRule()

    override val chain = RuleChain.outerRule(injectRule)
        .around(rxIdlerRule)
        .around(activityScenarioRule)!!
}
