package ru.olegivo.afs.helpers

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.verify

inline fun <reified TMock : Any, reified TCaptureResult : Any> TMock.capture(block: TMock.(TCaptureResult) -> Unit): TCaptureResult {
    val argumentCaptor = argumentCaptor<TCaptureResult>()
    verify(this).block(argumentCaptor.capture())
    return argumentCaptor.allValues.single()
}