package ru.olegivo.afs.common.domain

interface ErrorReporter {
    fun reportError(error: Throwable, message: String)
}
