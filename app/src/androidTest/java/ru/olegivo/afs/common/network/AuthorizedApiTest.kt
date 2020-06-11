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

import com.squareup.moshi.Moshi
import io.reactivex.Single
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import ru.olegivo.afs.BuildConfig
import ru.olegivo.afs.auth.domain.AuthRepository
import ru.olegivo.afs.auth.network.AccessTokenInterceptor
import java.util.*

open class AuthorizedApiTest {
    private fun createMoshi(): Moshi = Moshi.Builder()
        .add(Date::class.java, DateJsonAdapter().nullSafe())
//        .add(UUID::class.java, UuidJsonAdapter().nullSafe())
        .build()

    protected val moshi by lazy { createMoshi() }

    protected val api: Api by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            //level = HttpLoggingInterceptor.Level.BASIC
            level = HttpLoggingInterceptor.Level.BODY
        }

        val authRepository = StubAuthRepository()
        val accessTokenInterceptor = AccessTokenInterceptor(authRepository)

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(accessTokenInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.API_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okHttpClient)
            .build()

        retrofit.create(Api::class.java)
    }

    class StubAuthRepository : AuthRepository {
        override fun getAccessToken(): Single<String> {
            return Single.just("6e614760bed07f246778ee614004232d")
        }
    }
}
