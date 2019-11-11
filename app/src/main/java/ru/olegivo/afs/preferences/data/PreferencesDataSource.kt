package ru.olegivo.afs.preferences.data

import io.reactivex.Completable
import io.reactivex.Maybe

interface PreferencesDataSource {
    fun getString(key: String): Maybe<String>
    fun putString(key: String, value: String): Completable
    fun getInt(key: String): Maybe<Int>
    fun putInt(key: String, value: Int): Completable
    fun getBoolean(key: String): Maybe<Boolean>
    fun putBoolean(key: String, value: Boolean): Completable
}