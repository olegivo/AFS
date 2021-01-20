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

class FakeBaseRxDao<T, TKey>(entities: MutableMap<TKey, T>, getKey: T.() -> TKey) : BaseRxDao<T> {
    private val dao = FakeBaseDao(entities, getKey)
    override fun insertCompletable(vararg obj: T) =
        { dao.insert(*obj) }.toCompletable()

    override fun upsertCompletable(objects: List<T>) =
        { dao.upsert(objects) }.toCompletable()
}
