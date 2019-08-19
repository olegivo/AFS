package ru.olegivo.afs.preferences.data

import io.reactivex.Completable
import io.reactivex.Maybe

interface PreferencesDataSource {
    fun saveAccessToken(accessToken: String): Completable
    fun getAccessToken(): Maybe<String>
}