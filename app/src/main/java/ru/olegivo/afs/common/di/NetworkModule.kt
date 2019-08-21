package ru.olegivo.afs.common.di

import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import ru.olegivo.afs.BuildConfig
import ru.olegivo.afs.auth.domain.AuthRepository
import ru.olegivo.afs.auth.network.AccessTokenInterceptor
import ru.olegivo.afs.common.network.Api
import javax.inject.Singleton

@Module
class NetworkModule {
    @Provides
    @Singleton
    fun provideApi(retrofit: Retrofit): Api = retrofit.create(Api::class.java)

    @Provides
    fun providesRetrofit(moshi: Moshi, okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.API_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okHttpClient)
            .build()

    @Provides
    fun providesOkHttpClient(authRepository: AuthRepository): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
            .apply {
                level = HttpLoggingInterceptor.Level.BASIC
                // level = HttpLoggingInterceptor.Level.BODY
            }

        val accessTokenInterceptor = AccessTokenInterceptor(authRepository)

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(accessTokenInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun providesMoshi(): Moshi = Moshi.Builder()
//        .add(Date::class.java, DateJsonAdapter().nullSafe())
//        .add(UUID::class.java, UuidJsonAdapter().nullSafe())
        .build()
}
