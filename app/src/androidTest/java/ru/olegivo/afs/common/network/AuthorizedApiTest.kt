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

package ru.olegivo.afs.common.network

import io.reactivex.Single
import ru.olegivo.afs.BaseTest
import ru.olegivo.afs.auth.domain.AuthRepository
import ru.olegivo.afs.common.di.NetworkModule

open class AuthorizedApiTest : BaseTest() {

    private val authRepository by lazy { StubAuthRepository() }
    protected val api: Api by lazy { createKtorApi(authRepository) }
    protected val networkErrorsMapper by lazy { NetworkErrorsMapper(json) }
    private val json = NetworkModule.ProvidesKtorModule.providesJson()

    override fun getAllMocks(): Array<Any> = emptyArray()

    private fun createKtorApi(authRepository: StubAuthRepository): ApiImpl {
        val okHttpClient = NetworkModule.ProvidesModule.providesOkHttpClient(
            authRepository = authRepository,
            additionalInterceptors = { emptySet() }
        )
        val httpClient = NetworkModule.ProvidesKtorModule.providesHttpClient(okHttpClient, json)
        return ApiImpl(httpClient)
    }

    class StubAuthRepository : AuthRepository {
        override fun getAccessToken(): Single<String> {
            return Single.just("6e614760bed07f246778ee614004232d")
        }
    }
}
