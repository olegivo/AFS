/*
 * Copyright (C) 2021 Oleg Ivashchenko <olegivo@gmail.com>
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

package ru.olegivo.afs.common.network

import io.ktor.utils.io.core.use
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import ru.olegivo.afs.common.domain.ErrorWrapper
import ru.olegivo.afs.common.domain.HttpCallFailureException
import ru.olegivo.afs.common.domain.HttpNotFoundException
import ru.olegivo.afs.common.domain.NoNetworkException
import ru.olegivo.afs.common.domain.ServerUnreachableException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject
import kotlin.reflect.KClass

class NetworkErrorsMapper @Inject constructor(private val json: Json) {
    inline fun <reified E : ErrorWrapper> mapError(error: Throwable): Throwable {
        return when {
            error is SocketTimeoutException -> {
                NoNetworkException(error)
            }
            error is UnknownHostException -> {
                ServerUnreachableException(error)
            }
            error is HttpException && error.code() == notFound -> {
                mapErrorBody(error, E::class)?.let {
                    HttpNotFoundException(it.getError(), error)
                } ?: IllegalStateException("Mapping http body failed!")
            }
            error is HttpException && error.code() >= errorCodes -> {
                mapErrorBody(error, E::class)?.let {
                    HttpCallFailureException(it.getError(), error)
                } ?: IllegalStateException("Mapping http body failed!")
            }
            else -> {
                error
            }
        }
    }

    fun <T : Any> mapErrorBody(error: HttpException, type: KClass<T>) =
        error.response()?.errorBody()
            ?.let { errorBody ->
                json.serializersModule.getContextual(type)
                    ?.let { deserializer ->
                        errorBody.use {
                            json.decodeFromString(deserializer, it.string())
                        }
                    }
            }

    companion object {
        const val notFound = 404
        const val errorCodes = 400
    }
}

suspend inline fun <reified E : ErrorWrapper, T : Any> mapCoroutineError(
    networkErrorsMapper: NetworkErrorsMapper,
    crossinline block: suspend () -> T
) =
    try {
        block()
    } catch (error: Throwable) {
        throw networkErrorsMapper.mapError<E>(error)
    }
