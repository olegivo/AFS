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

package ru.olegivo.afs.errors

import io.reactivex.exceptions.UndeliverableException
import io.reactivex.plugins.RxJavaPlugins
import timber.log.Timber
import java.io.IOException
import java.net.SocketException

class UncaughtException private constructor() : Thread.UncaughtExceptionHandler {
    private val rootHandler: Thread.UncaughtExceptionHandler

    init {
        Timber.tag(javaClass.simpleName)
        rootHandler = Thread.getDefaultUncaughtExceptionHandler()!!

        Thread.setDefaultUncaughtExceptionHandler(this)
        RxJavaPlugins.setErrorHandler(this::onRxError)
    }

    override fun uncaughtException(t: Thread, e: Throwable) {
        Timber.e(e, "Uncaught Exception in t '%s'", t.name)
        rootHandler.uncaughtException(t, e)
    }

    private fun onRxError(ex: Throwable) {

        when (val e = if (ex is UndeliverableException) ex.cause!! else ex) {
            is IOException, is SocketException, /*fine, irrelevant network problem or API that throws on cancellation*/
            is InterruptedException /*fine, some blocking code was interrupted by a dispose call*/ -> {
            }

            is NullPointerException, is IllegalArgumentException /*that's likely a bug in the application*/,
            is IllegalStateException /*that's a bug in RxJava or in a custom operator*/ -> {
                Thread.currentThread()
                    .uncaughtExceptionHandler!!.uncaughtException(Thread.currentThread(), e)
            }

            else -> {
                Timber.w(e, "Undeliverable exception received, not sure what to do")
            }
        }
    }

    companion object {
        private lateinit var instance: UncaughtException

        fun setup() {
            instance = UncaughtException()
        }
    }
}
