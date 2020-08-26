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

package ru.olegivo.afs.helpers

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.stub
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.willReturn
import io.reactivex.Completable
import org.mockito.BDDMockito

inline fun <reified TMock : Any, reified TCaptureResult : Any> TMock.capture(block: TMock.(TCaptureResult) -> Unit): TCaptureResult {
    val argumentCaptor = argumentCaptor<TCaptureResult>()
    verify(this).block(argumentCaptor.capture())
    return argumentCaptor.allValues.single()
}

fun BDDMockito.BDDMyOngoingStubbing<Completable>.willComplete() =
    willReturn { Completable.complete() }

class BlockingOngoingStubbing<T : Any, R>(val mock: T, val m: suspend T.() -> R)

fun <T : Any, R> givenBlocking(mock: T, m: suspend T.() -> R) =
    BlockingOngoingStubbing<T, R>(mock, m)

fun <T : Any, R> BlockingOngoingStubbing<T, R>.willReturn(result: () -> R) {
    mock.stub {
        this.onBlocking(m).thenReturn(result())
    }
}
