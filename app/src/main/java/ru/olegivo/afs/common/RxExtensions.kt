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

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single

fun Completable.andThenDefer(completableProvider: () -> Completable) =
    andThen(Completable.defer(completableProvider))

fun <T : Any> Completable.andThenDeferSingle(singleProvider: () -> Single<T>): Single<T> =
    andThen(Single.defer(singleProvider))

fun <T : Any> Completable.andThenDeferMaybe(maybeProvider: () -> Maybe<T>): Maybe<T> =
    andThen(Maybe.defer(maybeProvider))
