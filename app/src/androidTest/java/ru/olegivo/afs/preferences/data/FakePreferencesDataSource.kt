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

package ru.olegivo.afs.preferences.data

import io.reactivex.rxkotlin.toCompletable
import ru.olegivo.afs.extensions.toMaybe

class FakePreferencesDataSource : PreferencesDataSource {

    private val map = mutableMapOf<String, Any>()

    override fun getString(key: String) = (map[key] as String?).toMaybe()

    override fun putString(key: String, value: String) =
        { map[key] = value }.toCompletable()

    override fun getInt(key: String) = (map[key] as Int?).toMaybe()

    override fun putInt(key: String, value: Int) =
        { map[key] = value }.toCompletable()

    override fun getLong(key: String) = (map[key] as Long?).toMaybe()

    override fun putLong(key: String, value: Long) =
        { map[key] = value }.toCompletable()

    override fun getBoolean(key: String) = (map[key] as Boolean?).toMaybe()

    override fun putBoolean(key: String, value: Boolean) =
        { map[key] = value }.toCompletable()

    fun reset() {
        map.clear()
    }
}
