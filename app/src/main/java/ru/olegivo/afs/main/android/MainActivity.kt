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

package ru.olegivo.afs.main.android

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import dagger.android.AndroidInjection
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import ru.olegivo.afs.R
import ru.olegivo.afs.common.di.ScopedFragmentFactory
import ru.olegivo.afs.analytics.domain.AnalyticsProvider
import ru.olegivo.afs.analytics.domain.ScreenNameProvider
import ru.olegivo.afs.analytics.models.AnalyticEvent
import ru.olegivo.afs.favorites.android.getExtraFavoriteRecordReminderParameters
import ru.olegivo.afs.favorites.android.putFavoriteRecordReminderParameters
import ru.olegivo.afs.favorites.domain.models.FavoriteRecordReminderParameters
import ru.olegivo.afs.home.android.HomeScreen
import ru.olegivo.afs.schedule.presentation.models.ReserveDestination
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router
import ru.terrakok.cicerone.android.support.SupportAppNavigator
import javax.inject.Inject

class MainActivity : AppCompatActivity(), HasAndroidInjector {
    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    @Inject
    lateinit var navigatorHolder: NavigatorHolder

    @Inject
    lateinit var router: Router

    @Inject
    lateinit var appNavigator: ru.olegivo.afs.common.presentation.Navigator

    @Inject
    lateinit var scopedFragmentFactory: ScopedFragmentFactory

    @Inject
    lateinit var analyticsProvider: AnalyticsProvider

    private val navigator: Navigator =
        object : SupportAppNavigator(this, supportFragmentManager, R.id.container) {
        }

    private val fragmentLifecycleCallbacks = object :
        FragmentManager.FragmentLifecycleCallbacks() {

        override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
            super.onFragmentResumed(fm, f)
            (f as? ScreenNameProvider)?.let { screen ->
                analyticsProvider.logEvent(
                    AnalyticEvent.ScreenView(
                        screenName = screen.screenName,
                        screenClass = screen.javaClass.simpleName,
                        parameters = screen.parameters
                    )
                ).subscribe()
            }
        }
    }

    override fun androidInjector() = androidInjector

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        supportFragmentManager.fragmentFactory = scopedFragmentFactory
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        router.newRootScreen(HomeScreen)
        processIntent(intent)
    }

    override fun onStart() {
        super.onStart()
        supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentLifecycleCallbacks, true)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let { processIntent(it) }
    }

    private fun processIntent(intent: Intent) {
        if (intent.action == ACTION_SHOW_SPORTS_ACTIVITY_DETAILS) {
            appNavigator.navigateTo(
                intent.getExtraFavoriteRecordReminderParameters()
                    .let {
                        ReserveDestination(
                            id = it.scheduleId,
                            clubId = it.clubId
                        )
                    }
            )
        }
    }

    override fun onResumeFragments() {
        super.onResume()
        navigatorHolder.setNavigator(navigator)
    }

    override fun onPause() {
        navigatorHolder.removeNavigator()
        super.onPause()
    }

    companion object {
        private const val ACTION_SHOW_SPORTS_ACTIVITY_DETAILS =
            "ACTION_SHOW_SPORTS_ACTIVITY_DETAILS"

        fun createIntent(
            context: Context,
            reminderParameters: FavoriteRecordReminderParameters
        ): PendingIntent =
            Intent(context, MainActivity::class.java)
                .setAction(ACTION_SHOW_SPORTS_ACTIVITY_DETAILS)
                .putFavoriteRecordReminderParameters(reminderParameters)
                .let { intent ->
                    PendingIntent.getActivity(context, 0, intent, 0)
                }
    }
}
