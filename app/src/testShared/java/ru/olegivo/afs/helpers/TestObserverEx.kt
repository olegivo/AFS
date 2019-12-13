package ru.olegivo.afs.helpers

import io.reactivex.observers.TestObserver

fun <Input> TestObserver<Input>.getSingleValue(): Input {
    return assertNoErrors()
        .values()
        .single()
}

fun <T> TestObserver<T>.checkSingleValue(block: (T) -> Unit) {
    block(values().single())
}
