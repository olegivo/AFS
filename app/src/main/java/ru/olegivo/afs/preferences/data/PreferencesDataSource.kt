package ru.olegivo.afs.preferences.data

import io.reactivex.Completable
import io.reactivex.Maybe

interface PreferencesDataSource {
    fun getString(key: String): Maybe<String>
    fun putString(key: String, value: String): Completable
}