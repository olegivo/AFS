/*
 * Copyright (C) 2020 Oleg Ivashchenko <olegivo@gmail.com>
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

package ru.olegivo.afs.auth.network

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import ru.olegivo.afs.auth.domain.AuthRepository
import java.io.IOException
import javax.inject.Inject

class AccessTokenInterceptor @Inject constructor(
    private val authRepository: AuthRepository
) : Interceptor {

    private var onUnauthorizedError: (() -> Unit)? = null

    fun setUnauthorizedCallBack(onUnauthorizedError: (() -> Unit)) {
        this.onUnauthorizedError = onUnauthorizedError
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = prepareRequest(chain)
        val request = wrapWithAuth(originalRequest)
        val response = chain.proceed(request)
        return handleUnauthorizedResponse(response, chain, originalRequest)
    }

    private fun prepareRequest(chain: Interceptor.Chain): Request {
        return chain.request().newBuilder()
            .addHeader("Accept", ACCEPT)
            .addHeader("Referer", REFERER)
            .addHeader("User-Agent", USER_AGENT)
            .addHeader("X-Requested-With", "XMLHttpRequest")
            .build()
    }

    private fun wrapWithAuth(originalRequest: Request): Request {
        val authRequired = true/* TODO:
        !(
        originalRequest.url().encodedPathSegments().any {
            it.equals("token", ignoreCase = true)
        } || !prefsRepository.isLoggedIn()
        )
        */
        return if (authRequired) {
            requestWithToken(originalRequest)
        } else {
            originalRequest
        }
    }

    private fun handleUnauthorizedResponse(
        response: Response,
        chain: Interceptor.Chain,
        originalRequest: Request
    ): Response {
        val hasUnauthorizedError = onUnauthorizedError != null &&
            response.code == HTTP_CODE_UNAUTHORIZED /*&&
                    originalRequest.url().encodedPathSegments().all { !it.equals("logout", ignoreCase = true) }*/
        return if (hasUnauthorizedError) {
            onUnauthorizedError!!.invoke()
            chain.proceed(requestWithToken(originalRequest))
        } else {
            response
        }
    }

    private fun requestWithToken(originalRequest: Request): Request {
        val accessToken = authRepository.getAccessToken().blockingGet()
        return originalRequest.newBuilder()
            .header(AUTHORIZATION_KEY, "Bearer $accessToken")
            .build()
    }

    companion object {
        private const val AUTHORIZATION_KEY = "Authorization"
        private const val HTTP_CODE_UNAUTHORIZED = 401
        private const val REFERER: String =
            "https://mobifitness.ru/widget/537535?colored=1&lines=1&club=375&clubs=0&grid30min=0&desc=0&direction=0&group=0&trainer=0&room=0&age=&level=&activity=0&language=ru&custom_css=0&category_filter=2&activity_filter=2&render_type=0&disable_booking=0&icons=&week=0&year=0&filters=groups,activities,trainers&filtercolor=000000&primarycolor=d3222c&filtertextcolor=ffffff&background=&datas=category_filter,colored,lines,desc"
        private const val USER_AGENT: String =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.142 Safari/537.36"
        private const val ACCEPT = "application/json, text/javascript, */*; q=0.01"
    }
}
