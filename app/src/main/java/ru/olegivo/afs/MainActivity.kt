package ru.olegivo.afs

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.android.AndroidInjection
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import ru.olegivo.afs.favorites.android.getExtraFavoriteRecordReminderParameters
import ru.olegivo.afs.favorites.android.putFavoriteRecordReminderParameters
import ru.olegivo.afs.favorites.domain.models.FavoriteRecordReminderParameters
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

    private val navigator: Navigator =
        object : SupportAppNavigator(this, supportFragmentManager, R.id.container) {
        }

    override fun androidInjector() = androidInjector

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        router.newRootScreen(MainScreen)
        processIntent(intent)
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
