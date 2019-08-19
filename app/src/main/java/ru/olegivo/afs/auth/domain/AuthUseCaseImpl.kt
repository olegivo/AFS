package ru.olegivo.afs.auth.domain

import io.reactivex.Completable
import javax.inject.Inject

class AuthUseCaseImpl @Inject constructor(private val authRepository: AuthRepository) :
    AuthUseCase {
    override fun invoke(): Completable {
        return authRepository.getAccessToken()
            .ignoreElement()
    }
}