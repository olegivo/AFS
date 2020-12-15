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

package ru.olegivo.afs.settings.domain

import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Test
import ru.olegivo.afs.BaseTestOf
import ru.olegivo.afs.helpers.willComplete

class DeleteDatabaseUseCaseImplTest : BaseTestOf<DeleteDatabaseUseCase>() {

    override fun createInstance() = DeleteDatabaseUseCaseImpl(databaseHelper)

    private val databaseHelper: DatabaseHelper = mock()

    override fun getAllMocks(): Array<Any> = arrayOf(databaseHelper)

    @Test
    fun `invoke DELEGATES deletion to databaseHelper`() {
        given { databaseHelper.delete() }.willComplete()

        instance.invoke()
            .andTriggerActions()
            .assertSuccess()

        verify(databaseHelper).delete()
    }
}
