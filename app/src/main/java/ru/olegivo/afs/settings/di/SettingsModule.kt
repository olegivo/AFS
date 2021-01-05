/*
 * Copyright (C) 2021 Oleg Ivashchenko <olegivo@gmail.com>
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

package ru.olegivo.afs.settings.di

import dagger.Binds
import dagger.Module
import ru.olegivo.afs.common.di.PerFragment
import ru.olegivo.afs.settings.domain.DeleteDatabaseUseCase
import ru.olegivo.afs.settings.domain.DeleteDatabaseUseCaseImpl
import ru.olegivo.afs.settings.presentation.SettingsContract
import ru.olegivo.afs.settings.presentation.SettingsPresenter

@Module
interface SettingsModule {
    @PerFragment
    @Binds
    fun bindSettingsPresenter(impl: SettingsPresenter): SettingsContract.Presenter

    @Binds
    fun bindDeleteDatabaseUseCase(impl: DeleteDatabaseUseCaseImpl): DeleteDatabaseUseCase
}
