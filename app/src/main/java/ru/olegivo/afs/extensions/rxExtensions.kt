package ru.olegivo.afs.extensions

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single

fun <T> Single<T>.andThen(completable: (T) -> Completable): Single<T> {
    return this.flatMap { completable(it).andThen(Single.just(it)) }
}

fun <T : Any> T?.toMaybe() =
    Maybe.defer<T> {
        this?.let { Maybe.just(it) }
            ?: Maybe.empty()
    }
