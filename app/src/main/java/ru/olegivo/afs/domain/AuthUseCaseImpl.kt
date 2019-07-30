package ru.olegivo.afs.domain

import io.reactivex.Completable
import ru.olegivo.afs.domain.auth.AuthRepository
import javax.inject.Inject

class AuthUseCaseImpl @Inject constructor(private val authRepository: AuthRepository) : AuthUseCase {
    override fun invoke(): Completable {
        return authRepository.getAccessToken()
            .ignoreElement()
    }
}