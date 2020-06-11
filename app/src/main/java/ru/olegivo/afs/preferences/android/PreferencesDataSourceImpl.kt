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

package ru.olegivo.afs.preferences.android

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Scheduler
import ru.olegivo.afs.preferences.data.PreferencesDataSource
import javax.inject.Inject
import javax.inject.Named

class PreferencesDataSourceImpl @Inject constructor(
    @Named("application") private val context: Context,
    @Named("io") private val ioScheduler: Scheduler
) :
    PreferencesDataSource {

    override fun getString(key: String) = getValueMaybe(key) { getString(it, null)!! }

    override fun getInt(key: String): Maybe<Int> = getValueMaybe(key) { getInt(it, 0) }

    override fun getLong(key: String): Maybe<Long> = getValueMaybe(key) { getLong(it, 0) }

    override fun getBoolean(key: String): Maybe<Boolean> =
        getValueMaybe(key) { this.getBoolean(it, false) }

    private inline fun <T> getValueMaybe(
        key: String,
        crossinline getValue: SharedPreferences.(String) -> T
    ): Maybe<T> = Maybe.create<T> {
        val preferences = getSharedPreferences()
        if (preferences.contains(key)) {
            val value = preferences.getValue(key)
            it.onSuccess(value)
        }
        it.onComplete()
    }.subscribeOn(ioScheduler)

    override fun putString(key: String, value: String) = edit { putString(key, value) }

    override fun putInt(key: String, value: Int): Completable = edit { putInt(key, value) }

    override fun putLong(key: String, value: Long): Completable = edit { putLong(key, value) }

    override fun putBoolean(key: String, value: Boolean): Completable =
        edit { putBoolean(key, value) }

    private fun edit(block: SharedPreferences.Editor.() -> Unit) =
        Completable.fromCallable {
            getSharedPreferences().edit().apply(block).apply()
        }.subscribeOn(ioScheduler)

    private fun getSharedPreferences(): SharedPreferences {
        return context.getSharedPreferences(sharedPreferencesFileName, MODE_PRIVATE)
    }

    companion object {
        private const val sharedPreferencesFileName = "sharedPreferences"
    }
}
