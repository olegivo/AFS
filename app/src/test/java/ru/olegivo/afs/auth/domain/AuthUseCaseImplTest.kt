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
