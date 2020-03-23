package ru.olegivo.afs.logs.android

import android.os.Build
import ru.olegivo.afs.logs.domain.Logger
import timber.log.Timber

class LoggerImpl constructor(private val tagResolver: () -> String) : Logger {
    override fun log(message: String, tag: String?) {
        Timber.tag(tag.asSafeTag())
    }

    private fun String?.asSafeTag(): String {
        val tag = this ?: tagResolver.invoke()
        // Tag length limit was removed in API 24.
        if (Build.VERSION.SDK_INT < 24 && tag.length > MAX_TAG_LENGTH) {
            return tag.substring(0, MAX_TAG_LENGTH)
        }
        return tag
    }
}

private const val MAX_TAG_LENGTH = 23