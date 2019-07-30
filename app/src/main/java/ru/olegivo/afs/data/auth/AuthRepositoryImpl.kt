package ru.olegivo.afs.data.auth

import io.reactivex.Single
import ru.olegivo.afs.domain.auth.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor() :
    AuthRepository {

    override fun getAccessToken(): Single<String> {
        return Single.just(accessToken)
    }

    companion object {
        const val accessToken = "6e614760bed07f246778ee614004232d"
    }
}