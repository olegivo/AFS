package ru.olegivo.afs.domain.auth

import io.reactivex.Single

interface AuthRepository {
    fun getAccessToken(): Single<String>
}
