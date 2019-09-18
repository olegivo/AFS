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
