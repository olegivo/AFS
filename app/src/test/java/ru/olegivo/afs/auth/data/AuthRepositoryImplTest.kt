/*
 * Copyright (C) 2020 Oleg Ivashchenko <olegivo@gmail.com>
 *
 * This file is part of AFS.
 *
 * AFS is free software: you can redistribute it and/or modify
 * it under the terms of the MIT License.
 *
 * AFS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * AFS.
 */

package ru.olegivo.afs.auth.data

import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.Completable
import io.reactivex.Maybe
import org.junit.Test
import ru.olegivo.afs.BaseTest
import ru.olegivo.afs.auth.domain.AuthRepository
import ru.olegivo.afs.helpers.getRandomString
import ru.olegivo.afs.preferences.data.PreferencesDataSource

class AuthRepositoryImplTest : BaseTest() {
    override fun getAllMocks(): Array<Any> = arrayOf(preferencesDataSource)

    private val preferencesDataSource: PreferencesDataSource = mock()

    private val authRepository: AuthRepository = AuthRepositoryImpl(preferencesDataSource)

    @Test
    fun `getApiKey RETURNS hardcoded accessToken WHEN accessToken not saved`() {
        given(preferencesDataSource.getString(AuthRepositoryImpl.KEY_ACCESS_TOKEN)).willReturn(Maybe.empty())
        val accessToken = AuthRepositoryImpl.accessToken
        given(
            preferencesDataSource.putString(
                AuthRepositoryImpl.KEY_ACCESS_TOKEN,
                accessToken
            )
        ).willReturn(Completable.complete())

        authRepository.getAccessToken().test()
            .assertNoErrors()
            .assertValue(accessToken)

        verify(preferencesDataSource).getString(AuthRepositoryImpl.KEY_ACCESS_TOKEN)
        verify(preferencesDataSource).putString(AuthRepositoryImpl.KEY_ACCESS_TOKEN, accessToken)
    }

    @Test
    fun `getApiKey RETURNS saved accessToken WHEN accessToken saved`() {
        val accessToken = getRandomString()
        given(preferencesDataSource.getString(AuthRepositoryImpl.KEY_ACCESS_TOKEN)).willReturn(Maybe.just(accessToken))

        authRepository.getAccessToken().test()
            .assertNoErrors()
            .assertValue(accessToken)

        verify(preferencesDataSource).getString(AuthRepositoryImpl.KEY_ACCESS_TOKEN)
    }
}
