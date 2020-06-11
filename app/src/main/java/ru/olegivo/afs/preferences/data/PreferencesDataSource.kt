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

import io.reactivex.Completable
import io.reactivex.Maybe

interface PreferencesDataSource {
    fun getString(key: String): Maybe<String>
    fun putString(key: String, value: String): Completable
    fun getInt(key: String): Maybe<Int>
    fun putInt(key: String, value: Int): Completable
    fun getLong(key: String): Maybe<Long>
    fun putLong(key: String, value: Long): Completable
    fun getBoolean(key: String): Maybe<Boolean>
    fun putBoolean(key: String, value: Boolean): Completable
}
