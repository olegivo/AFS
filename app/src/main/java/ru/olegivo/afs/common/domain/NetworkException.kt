package ru.olegivo.afs.common.domain

open class NetworkException(message: String?, error: Throwable) : RuntimeException(message, error) {
    constructor(error: Throwable) : this(null, error)
}

class NoNetworkException(error: Throwable) : NetworkException(error)

class ServerUnreachableException(error: Throwable) : NetworkException(error)

class HttpCallFailureException(message: String?, error: Throwable) : NetworkException(message, error) {
    override val message: String
        get() = super.message!!
}

class HttpNotFoundException(message: String?, error: Throwable) : NetworkException(message, error) {
    override val message: String
        get() = super.message!!
}
