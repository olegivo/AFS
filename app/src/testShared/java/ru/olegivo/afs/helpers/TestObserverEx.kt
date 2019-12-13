package ru.olegivo.afs.helpers

import io.reactivex.observers.TestObserver

fun <Input> TestObserver<Input>.getSingleValue(): Input {
    return assertNoErrors()
        .values()
        .single()
}

