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

package ru.olegivo.afs.extensions

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Scheduler
import io.reactivex.Single

fun <T> Single<T>.andThen(completable: (T) -> Completable): Single<T> {
    return this.flatMap { completable(it).andThen(Single.just(it)) }
}

fun <T : Any> T?.toMaybe() =
    Maybe.defer<T> {
        this?.let { Maybe.just(it) }
            ?: Maybe.empty()
    }

fun <T : Any> T.toSingle() =
    Single.defer<T> { Single.just(this) }

fun <T : Any> (() -> T).toSingle() =
    Single.defer<T> {
        Single.just(this())
    }

fun <T : Any, R : Any> Single<List<T>>.mapList(itemSelector: (T) -> R) =
    map { list -> list.map(itemSelector) }

fun <I, T, R> Single<I>.parallelMap(
    scheduler: Scheduler,
    flattenSelector: (I) -> Iterable<T>,
    mapper: (T) -> R
): Single<List<R>> = flattenAsFlowable(flattenSelector)
    .parallel()
    .runOn(scheduler)
    .map(mapper)
    .sequential()
    .toList()

fun <T, R> Single<List<T>>.parallelMapList(
    scheduler: Scheduler,
    mapper: (T) -> R
): Single<List<R>> = parallelMap(scheduler, { it }, mapper)

fun <I, T, R> Maybe<I>.parallelMap(
    scheduler: Scheduler,
    flattenSelector: (I) -> Iterable<T>,
    mapper: (T) -> R
): Maybe<List<R>> =
    flatMap {
        flattenSelector(it).let { iterable ->
            if (iterable.any()) {
                Flowable.fromIterable(iterable)
                    .parallelMap(scheduler, mapper)
                    .toList()
                    .toMaybe()
            } else {
                Maybe.empty()
            }
        }
    }

fun <T, R> Maybe<List<T>>.parallelMapList(
    scheduler: Scheduler,
    mapper: (T) -> R
): Maybe<List<R>> = parallelMap(scheduler, { it }, mapper)

fun <T, R> Flowable<T>.parallelMap(scheduler: Scheduler, mapper: (T) -> R): Flowable<R> =
    parallel()
        .runOn(scheduler)
        .map(mapper)
        .sequential()
