package ru.olegivo.afs.data.auth

import org.junit.Test
import ru.olegivo.afs.BaseTest
import ru.olegivo.afs.domain.auth.AuthRepository

class AuthRepositoryImplTest : BaseTest() {
    override fun getAllMocks(): Array<Any> = arrayOf()

    private val authRepository: AuthRepository = AuthRepositoryImpl()

    @Test
    fun `getApiKey RETURNS hardcoded accessToken`() {
        authRepository.getAccessToken().test()
            .assertNoErrors()
            .assertValue(AuthRepositoryImpl.accessToken)
    }
}