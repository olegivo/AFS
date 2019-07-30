package ru.olegivo.afs.android.preferences

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.schedulers.Schedulers
import ru.olegivo.afs.data.preferences.PreferencesDataSource
import javax.inject.Inject

class PreferencesDataSourceImpl @Inject constructor(private val context: Context) : PreferencesDataSource {

    override fun saveAccessToken(accessToken: String): Completable {
        return Completable.fromCallable {
            val preferences = getSharedPreferences()
            preferences.edit().putString("accessToken", accessToken).apply()
        }.subscribeOn(Schedulers.io())
    }

    override fun getAccessToken(): Maybe<String> {
        return Maybe.create<String> { emitter ->
            val preferences = getSharedPreferences()
            preferences.getString("accessToken", null)?.let {
                emitter.onSuccess(it)
            }
            emitter.onComplete()
        }.subscribeOn(Schedulers.io())
    }

    private fun getSharedPreferences(): SharedPreferences {
        return context.getSharedPreferences(sharedPreferencesFileName, MODE_PRIVATE)
    }

    companion object {
        private const val sharedPreferencesFileName = "sharedPreferences"
    }
}