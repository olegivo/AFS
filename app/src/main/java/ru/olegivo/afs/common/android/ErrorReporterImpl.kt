package ru.olegivo.afs.common.android

import ru.olegivo.afs.common.domain.ErrorReporter
import timber.log.Timber
import javax.inject.Inject

class ErrorReporterImpl @Inject constructor() : ErrorReporter {
    override fun reportError(error: Throwable, message: String) = Timber.e(error, message)
}