package ru.olegivo.afs.auth.domain

import io.reactivex.Single

interface AuthRepository {
    fun getAccessToken(): Single<String>
}
