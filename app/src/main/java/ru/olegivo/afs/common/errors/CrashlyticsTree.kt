package ru.olegivo.afs.common.errors

import android.util.Log
import com.crashlytics.android.Crashlytics
import timber.log.Timber

class CrashlyticsTree : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (priority == Log.VERBOSE || priority == Log.DEBUG) {
            return
        }
        Crashlytics.setInt(CRASHLYTICS_KEY_PRIORITY, priority)
        Crashlytics.setString(CRASHLYTICS_KEY_TAG, tag)
        Crashlytics.setString(CRASHLYTICS_KEY_MESSAGE, message)
        if (t != null) {
            Crashlytics.logException(t)
        } else {
            val formattedMessage: String = LogMessageHelper.format(priority, tag, message)
            Crashlytics.logException(StackTraceRecorder(formattedMessage))
        }
    }

    companion object {
        private const val CRASHLYTICS_KEY_PRIORITY = "priority"
        private const val CRASHLYTICS_KEY_TAG = "tag"
        private const val CRASHLYTICS_KEY_MESSAGE = "message"
    }
}

object LogMessageHelper {
    fun format(priority: Int, tag: String?, message: String): String {
        val messageWithTag = if (tag != null) "[$tag] $message" else message
        return prefixForPriority(priority) + messageWithTag
    }

    private fun prefixForPriority(priority: Int): String {
        return when (priority) {
            Log.VERBOSE -> "[VERBOSE] "
            Log.DEBUG -> "[DEBUG] "
            Log.INFO -> "[INFO] "
            Log.WARN -> "[WARN] "
            Log.ERROR -> "[ERROR] "
            Log.ASSERT -> "[ASSERT] "
            else -> "[UNKNOWN($priority)] "
        }
    }
}

class StackTraceRecorder(detailMessage: String) : Throwable(detailMessage) {

    override fun fillInStackTrace(): Throwable {
        super.fillInStackTrace()
        stackTrace = stackTrace
            .dropWhile { !isTimber(it) } // heading to top of Timber stack trace
            .dropWhile { isTimber(it) } // copy all but skip Timber
            .toTypedArray<StackTraceElement>()

        return this
    }

    private fun isTimber(stackTraceElement: StackTraceElement): Boolean {
        return stackTraceElement.className == Timber::class.java.name
    }
}