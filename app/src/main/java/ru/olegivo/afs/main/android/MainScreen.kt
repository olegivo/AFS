package ru.olegivo.afs.main.android

import ru.olegivo.afs.main.android.MainFragment
import ru.terrakok.cicerone.android.support.SupportAppScreen

object MainScreen : SupportAppScreen() {
    override fun getFragment() = MainFragment()
}
