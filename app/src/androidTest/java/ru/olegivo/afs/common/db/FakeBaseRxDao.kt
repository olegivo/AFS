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

package ru.olegivo.afs.common.db

import io.reactivex.rxkotlin.toCompletable

class FakeBaseRxDao<T, TKey>(
    private val entities: MutableMap<TKey, T>,
    private val getKey: T.() -> TKey
) : BaseRxDao<T> {
    override fun insertCompletable(vararg obj: T) =
        {
            obj.forEach {
                val key = it.getKey()
                if (entities.containsKey(key)) throw RuntimeException("Duplicate key $key. Aborting insert")
                entities[key] = it
            }
        }.toCompletable()

    override fun upsertCompletable(objects: List<T>) =
        {
            objects.forEach {
                entities[it.getKey()] = it
            }
        }.toCompletable()
}
