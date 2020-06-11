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

import io.reactivex.Single
import ru.olegivo.afs.preferences.data.PreferencesDataSource
import ru.olegivo.afs.auth.domain.AuthRepository
import ru.olegivo.afs.extensions.andThen
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(private val preferencesDataSource: PreferencesDataSource) :
    AuthRepository {

    override fun getAccessToken(): Single<String> {
        return preferencesDataSource.getString(KEY_ACCESS_TOKEN)
            .switchIfEmpty(
                Single.defer {
                    Single.just(accessToken)
                        .andThen { preferencesDataSource.putString(KEY_ACCESS_TOKEN, it) }
                }
            )
    }

    companion object {
        const val accessToken = "6e614760bed07f246778ee614004232d"
        const val KEY_ACCESS_TOKEN = "ACCESS_TOKEN"
    }
}
