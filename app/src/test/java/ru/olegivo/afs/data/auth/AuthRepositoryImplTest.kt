package ru.olegivo.afs.data.auth

import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.Completable
import io.reactivex.Maybe
import org.junit.Test
import ru.olegivo.afs.BaseTest
import ru.olegivo.afs.data.preferences.PreferencesDataSource
import ru.olegivo.afs.domain.auth.AuthRepository
import ru.olegivo.afs.helpers.getRandomString

class AuthRepositoryImplTest : BaseTest() {
    override fun getAllMocks(): Array<Any> = arrayOf(preferencesDataSource)

    private val preferencesDataSource: PreferencesDataSource = mock()

    private val authRepository: AuthRepository = AuthRepositoryImpl(preferencesDataSource)

    @Test
    fun `getApiKey RETURNS hardcoded accessToken WHEN accessToken not saved`() {
        given(preferencesDataSource.getAccessToken()).willReturn(Maybe.empty())
        given(preferencesDataSource.saveAccessToken(AuthRepositoryImpl.accessToken)).willReturn(Completable.complete())

        authRepository.getAccessToken().test()
            .assertNoErrors()
            .assertValue(AuthRepositoryImpl.accessToken)

        verify(preferencesDataSource).getAccessToken()
        verify(preferencesDataSource).saveAccessToken(AuthRepositoryImpl.accessToken)
    }

    @Test
    fun `getApiKey RETURNS saved accessToken WHEN accessToken saved`() {
        val accessToken = getRandomString()
        given(preferencesDataSource.getAccessToken()).willReturn(Maybe.just(accessToken))

        authRepository.getAccessToken().test()
            .assertNoErrors()
            .assertValue(accessToken)

        verify(preferencesDataSource).getAccessToken()
    }
}