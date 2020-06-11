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

import dagger.Binds
import dagger.Module
import dagger.Provides
import ru.olegivo.afs.common.android.NavigatorImpl
import ru.olegivo.afs.common.presentation.Navigator
import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router
import javax.inject.Singleton

@Module(includes = [NavigationModule.BindModule::class])
class NavigationModule {

    @Module
    companion object {
        @JvmStatic
        @Singleton
        @Provides
        fun provideCicerone(): Cicerone<Router> = Cicerone.create()

        @JvmStatic
        @Provides
        fun provideNavigatorHolder(cicerone: Cicerone<Router>): NavigatorHolder =
            cicerone.navigatorHolder

        @JvmStatic
        @Provides
        fun getRouter(cicerone: Cicerone<Router>): Router = cicerone.router
    }

    @Module
    interface BindModule {
        @Singleton
        @Binds
        fun bindNavigator(impl: NavigatorImpl): Navigator
    }
}
