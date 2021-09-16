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
import io.reactivex.Maybe
import io.reactivex.MaybeEmitter
import io.reactivex.MaybeOnSubscribe
import io.reactivex.Scheduler
import io.reactivex.annotations.CheckReturnValue
import io.reactivex.schedulers.Schedulers

@CheckReturnValue
@JvmOverloads
@JvmName("toMaybe")
fun <T : Any> Query<T>.asMaybe(scheduler: Scheduler = Schedulers.io()): Maybe<Query<T>> {
    return Maybe.create(MaybeQueryOnSubscribe(this)).observeOn(scheduler)
}

private class MaybeQueryOnSubscribe<T : Any>(
    private val query: Query<T>
) : MaybeOnSubscribe<Query<T>> {
    override fun subscribe(emitter: MaybeEmitter<Query<T>>) {
        val listenerAndDisposable =
            QueryListenerAndDisposableInt(query) { emitter.onSuccess(query) }
        emitter.setDisposable(listenerAndDisposable)
        query.addListener(listenerAndDisposable)
        emitter.onSuccess(query)
    }
}

@CheckReturnValue
fun <T : Any> Maybe<Query<T>>.mapToOne(): Maybe<T> {
    return map { it.executeAsOne() }
}

@CheckReturnValue
fun <T : Any> Maybe<Query<T>>.mapToList(): Maybe<List<T>> {
    return map { it.executeAsList() }
}
