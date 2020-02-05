package ru.olegivo.afs.common.android

import android.content.Context
import android.content.Intent
import android.net.Uri
import ru.olegivo.afs.common.presentation.BrowserDestination
import ru.terrakok.cicerone.android.support.SupportAppScreen

class BrowserScreen(private val destination: BrowserDestination) : SupportAppScreen() {
    override fun getActivityIntent(context: Context) =
        Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(destination.url)
        }
}
