package ru.olegivo.afs.preferences.android

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.schedulers.Schedulers
import ru.olegivo.afs.preferences.data.PreferencesDataSource
import javax.inject.Inject

class PreferencesDataSourceImpl @Inject constructor(private val context: Context) :
    PreferencesDataSource {
    override fun getString(key: String) =
        Maybe.create<String> { emitter ->
            val preferences = getSharedPreferences()
            preferences.getString(key, null)?.let {
                emitter.onSuccess(it)
            }
            emitter.onComplete()
        }.subscribeOn(Schedulers.io())

    override fun putString(keyAccessToken: String, value: String) =
        Completable.fromCallable {
            val preferences = getSharedPreferences()
            preferences.edit().putString(keyAccessToken, value).apply()
        }.subscribeOn(Schedulers.io())

    private fun getSharedPreferences(): SharedPreferences {
        return context.getSharedPreferences(sharedPreferencesFileName, MODE_PRIVATE)
    }

    companion object {
        private const val sharedPreferencesFileName = "sharedPreferences"
    }
}