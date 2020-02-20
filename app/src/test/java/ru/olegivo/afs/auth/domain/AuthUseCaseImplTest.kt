package ru.olegivo.afs.auth.domain

import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.Single
import org.junit.Test
import ru.olegivo.afs.BaseTest
import ru.olegivo.afs.helpers.getRandomString

class AuthUseCaseImplTest : BaseTest() {

    private val authRepository: AuthRepository = mock()

    private val authUseCase: AuthUseCase =
        AuthUseCaseImpl(authRepository)

    override fun getAllMocks(): Array<Any> = arrayOf(authRepository)

    @Test
    fun `invoke make network call and successfully completes`() {
        val accessToken = getRandomString()
        given(authRepository.getAccessToken()).willReturn(Single.just(accessToken))

        authUseCase().test()

        verify(authRepository).getAccessToken()
    }
}
