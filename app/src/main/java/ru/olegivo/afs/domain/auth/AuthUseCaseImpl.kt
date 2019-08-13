package ru.olegivo.afs.domain.auth

import io.reactivex.Completable
import javax.inject.Inject

class AuthUseCaseImpl @Inject constructor(private val authRepository: AuthRepository) :
    AuthUseCase {
    override fun invoke(): Completable {
        return authRepository.getAccessToken()
            .ignoreElement()
    }
}