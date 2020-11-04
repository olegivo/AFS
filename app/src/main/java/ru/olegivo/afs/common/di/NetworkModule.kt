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

package ru.olegivo.afs.common.di

import com.squareup.moshi.Moshi
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import ru.olegivo.afs.BuildConfig
import ru.olegivo.afs.auth.domain.AuthRepository
import ru.olegivo.afs.auth.network.AccessTokenInterceptor
import ru.olegivo.afs.common.network.Api
import ru.olegivo.afs.common.network.ApiImpl
import ru.olegivo.afs.common.network.FirebaseNetworkPerformanceInterceptor
import javax.inject.Provider
import javax.inject.Singleton

@Module(
    includes = [
        NetworkModule.ProvidesKtorModule::class
    ]
)
interface NetworkModule {
    @Binds
    @Singleton
    fun bindApi(impl: ApiImpl): Api

    @Binds
    @IntoSet
    fun bindFirebaseNetworkPerformanceInterceptor(
        firebaseNetworkPerformanceInterceptor: FirebaseNetworkPerformanceInterceptor
    ): Interceptor

    @Module(includes = [ProvidesModule::class])
    object ProvidesKtorModule {
        @Provides
        fun providesHttpClient(
            okHttpClient: OkHttpClient,
            json: Json
        ): HttpClient =
            HttpClient(OkHttp) {
                engine {
                    preconfigured = okHttpClient
                }
                install(JsonFeature) {
                    serializer = KotlinxSerializer(json)
                }
            }

        @Provides
        @Singleton
        fun providesJson(): Json =
            Json {
                ignoreUnknownKeys = true
                isLenient = true
                encodeDefaults = false
            }
    }

    @Module(includes = [ProvidesModule::class])
    object ProvidesRetrofitModule {
        @Provides
        fun providesRetrofit(moshi: Moshi, okHttpClient: OkHttpClient): Retrofit =
            Retrofit.Builder()
                .baseUrl(BuildConfig.API_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .client(okHttpClient)
                .build()

        @Provides
        @Singleton
        fun providesMoshi(): Moshi = Moshi.Builder()
//            .add(Date::class.java, DateJsonAdapter().nullSafe())
//        .add(UUID::class.java, UuidJsonAdapter().nullSafe())
            .build()
    }

    @Module
    object ProvidesModule {
        @Provides
        fun providesOkHttpClient(
            authRepository: AuthRepository,
            additionalInterceptors: Provider<Set<Interceptor>>
        ): OkHttpClient {
            val loggingInterceptor = HttpLoggingInterceptor()
                .apply {
                    level = HttpLoggingInterceptor.Level.BASIC
//                    level = HttpLoggingInterceptor.Level.BODY
                }

            val accessTokenInterceptor = AccessTokenInterceptor(authRepository)

            return OkHttpClient.Builder()
                .addInterceptor(accessTokenInterceptor)
                .addInterceptor(loggingInterceptor)
                .apply {
                    additionalInterceptors.get().forEach { addInterceptor(it) }
                }
                .build()
        }
    }
}
