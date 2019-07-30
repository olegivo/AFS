package ru.olegivo.afs.data.auth

import io.reactivex.Single
import ru.olegivo.afs.data.preferences.PreferencesDataSource
import ru.olegivo.afs.domain.auth.AuthRepository
import ru.olegivo.afs.extensions.andThen
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(private val preferencesDataSource: PreferencesDataSource) :
    AuthRepository {

    override fun getAccessToken(): Single<String> {
        return preferencesDataSource.getAccessToken()
            .switchIfEmpty(Single.defer {
                Single.just(accessToken)
                    .andThen { preferencesDataSource.saveAccessToken(it) }
            })

    }

    companion object {
        const val accessToken = "6e614760bed07f246778ee614004232d"
    }
}