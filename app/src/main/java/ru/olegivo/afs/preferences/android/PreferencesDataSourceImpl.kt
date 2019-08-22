package ru.olegivo.afs.preferences.android

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Scheduler
import io.reactivex.Single
import ru.olegivo.afs.preferences.data.PreferencesDataSource
import javax.inject.Inject
import javax.inject.Named

class PreferencesDataSourceImpl @Inject constructor(
    @Named("application") private val context: Context,
    @Named("io") private val ioScheduler: Scheduler
) :
    PreferencesDataSource {

    override fun getString(key: String) =
        Maybe.create<String> { emitter ->
            val preferences = getSharedPreferences()
            preferences.getString(key, null)?.let {
                emitter.onSuccess(it)
            }
            emitter.onComplete()
        }.subscribeOn(ioScheduler)

    override fun getInt(key: String, defaultValue: Int): Single<Int> =
        Single.fromCallable {
            val preferences = getSharedPreferences()
            preferences.getInt(key, defaultValue)
        }.subscribeOn(ioScheduler)

    override fun putString(key: String, value: String) = edit { putString(key, value) }

    override fun putInt(key: String, value: Int): Completable = edit { putInt(key, value) }

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