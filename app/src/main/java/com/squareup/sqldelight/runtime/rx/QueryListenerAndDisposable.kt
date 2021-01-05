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

package com.squareup.sqldelight.runtime.rx

import com.squareup.sqldelight.Query
import io.reactivex.disposables.Disposable
import java.util.concurrent.atomic.AtomicBoolean

internal class QueryListenerAndDisposable<T : Any>(
    private val query: Query<T>,
    private val emit: () -> Unit
) : AtomicBoolean(), Query.Listener, Disposable {
    override fun queryResultsChanged() {
        emit()
    }

    override fun isDisposed() = get()

    override fun dispose() {
        if (compareAndSet(false, true)) {
            query.removeListener(this)
        }
    }
}
