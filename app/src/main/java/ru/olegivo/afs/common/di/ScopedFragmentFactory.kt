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

package ru.olegivo.afs.common.di

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import dagger.MapKey
import dagger.android.DispatchingAndroidInjector
import javax.inject.Inject
import javax.inject.Provider
import kotlin.reflect.KClass

class ScopedFragmentFactory @Inject constructor(
    private val androidInjector: DispatchingAndroidInjector<Any>
) : FragmentFactory() {

    private val providers = FragmentProviders()

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        androidInjector.inject(providers)

        val clazz = loadFragmentClass(classLoader, className)
        return providers[clazz]?.get() ?: super.instantiate(classLoader, className)
    }

    class FragmentProviders {
        @Inject
        lateinit var fragmentProviderByClass: MutableMap<Class<out Fragment>, Provider<Fragment>>

        operator fun get(clazz: Class<out Fragment>) = fragmentProviderByClass[clazz]
    }
}

@MapKey
annotation class FragmentKey(val value: KClass<out Fragment>)
