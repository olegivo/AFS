package ru.olegivo.afs.preferences.data

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single

interface PreferencesDataSource {
    fun getString(key: String): Maybe<String>
    fun putString(key: String, value: String): Completable
    fun getInt(key: String, defaultValue: Int): Single<Int>
    fun putInt(key: String, value: Int): Completable
}