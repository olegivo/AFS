package ru.olegivo.afs.auth.data

import io.reactivex.Single
import ru.olegivo.afs.preferences.data.PreferencesDataSource
import ru.olegivo.afs.auth.domain.AuthRepository
import ru.olegivo.afs.extensions.andThen
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(private val preferencesDataSource: PreferencesDataSource) :
    AuthRepository {

    override fun getAccessToken(): Single<String> {
        return preferencesDataSource.getString(KEY_ACCESS_TOKEN)
            .switchIfEmpty(Single.defer {
                Single.just(accessToken)
                    .andThen { preferencesDataSource.putString(KEY_ACCESS_TOKEN, it) }
            })
    }

    companion object {
        const val accessToken = "6e614760bed07f246778ee614004232d"
        const val KEY_ACCESS_TOKEN = "ACCESS_TOKEN"
    }
}
