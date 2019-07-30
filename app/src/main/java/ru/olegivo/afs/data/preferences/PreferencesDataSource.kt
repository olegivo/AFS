package ru.olegivo.afs.data.preferences

import io.reactivex.Completable
import io.reactivex.Maybe

interface PreferencesDataSource {
    fun saveAccessToken(accessToken: String): Completable
    fun getAccessToken(): Maybe<String>
}