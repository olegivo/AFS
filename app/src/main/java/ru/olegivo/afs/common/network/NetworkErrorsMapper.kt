package ru.olegivo.afs.common.network

import com.squareup.moshi.Moshi
import io.reactivex.Completable
import retrofit2.HttpException
import ru.olegivo.afs.common.domain.ErrorWrapper
import ru.olegivo.afs.common.domain.HttpCallFailureException
import ru.olegivo.afs.common.domain.HttpNotFoundException
import ru.olegivo.afs.common.domain.NoNetworkException
import ru.olegivo.afs.common.domain.ServerUnreachableException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

class NetworkErrorsMapper @Inject constructor(private val moshi: Moshi) {
    inline fun <reified E : ErrorWrapper> mapError(error: Throwable): Throwable {
        return when {
            error is SocketTimeoutException -> {
                NoNetworkException(error)
            }
            error is UnknownHostException -> {
                ServerUnreachableException(error)
            }
            error is HttpException && error.code() == 404 -> {
                mapErrorBody(error, E::class.java)?.let {
                    HttpNotFoundException(it.getError(), error)
                } ?: IllegalStateException("Mapping http body failed!")
            }
            error is HttpException && error.code() >= 400 -> {
                mapErrorBody(error, E::class.java)?.let {
                    HttpCallFailureException(it.getError(), error)
                } ?: IllegalStateException("Mapping http body failed!")
            }
            else -> {
                error
            }
        }
    }

    fun <T> mapErrorBody(error: HttpException, type: Class<T>) =
        error.response()?.errorBody()
            ?.let { errorBody ->
                errorBody.use {
                    moshi.adapter<T>(type)
                        .fromJson(it.string())
                }
            }
}

inline fun <reified E : ErrorWrapper> Completable.mapCompletableError(networkErrorsMapper: NetworkErrorsMapper): Completable =
    onErrorResumeNext { error ->
        Completable.error(networkErrorsMapper.mapError<E>(error))
    }
