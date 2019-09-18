package ru.olegivo.afs

import ru.terrakok.cicerone.android.support.SupportAppScreen

object MainScreen : SupportAppScreen() {
    override fun getFragment() = MainFragment()
}
