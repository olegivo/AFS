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

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import ru.olegivo.afs.AfsApplication
import ru.olegivo.afs.auth.di.AuthModule
import ru.olegivo.afs.common.android.ErrorReporterImpl
import ru.olegivo.afs.common.domain.DateProvider
import ru.olegivo.afs.common.domain.DateProviderImpl
import ru.olegivo.afs.common.domain.DateTimeUtils
import ru.olegivo.afs.common.domain.DateTimeUtilsImpl
import ru.olegivo.afs.common.domain.ErrorReporter
import java.util.Locale
import javax.inject.Named
import javax.inject.Singleton

@Module(
    includes = [
        CoroutinesModule::class,
        RxModule::class,
        NavigationModule::class,
        AuthModule::class,
        AppModule.AppProvidesModule::class
    ]
)
interface AppModule {

    @Binds
    @Named("application")
    fun bindApplicationContext(app: AfsApplication): Context

    @Binds
    fun bindDateProvider(app: DateProviderImpl): DateProvider

    @Binds
    fun bindDateTimeUtils(app: DateTimeUtilsImpl): DateTimeUtils

    @Binds
    fun bindErrorReporter(app: ErrorReporterImpl): ErrorReporter

    @Module
    object AppProvidesModule {
        @Provides
        @Singleton
        fun providesLocale() = Locale("ru")
    }
}
