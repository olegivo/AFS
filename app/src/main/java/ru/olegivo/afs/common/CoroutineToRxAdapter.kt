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

package ru.olegivo.afs.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.rx2.rxCompletable
import kotlinx.coroutines.rx2.rxMaybe
import kotlinx.coroutines.rx2.rxSingle
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

class CoroutineToRxAdapter @Inject constructor() {
    var coroutineContext: CoroutineContext = EmptyCoroutineContext

    fun <T : Any> runToSingle(block: suspend CoroutineScope.() -> T) =
        rxSingle(coroutineContext, block)

    fun <T : Any> runToMaybe(block: suspend CoroutineScope.() -> T?) =
        rxMaybe(coroutineContext, block)

    fun runToCompletable(block: suspend CoroutineScope.() -> Unit) =
        rxCompletable(coroutineContext, block)
}
