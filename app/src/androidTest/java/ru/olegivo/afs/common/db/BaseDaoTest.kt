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

package ru.olegivo.afs.common.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import ru.olegivo.afs.BaseTest

abstract class BaseDaoTest<ROOM : RoomDatabase, DAO>(
    private val roomClass: Class<ROOM>,
    private val daoProvider: (ROOM) -> DAO
) : BaseTest() {
    @Rule
    @JvmField
    val instantTaskExecutorRule =
        InstantTaskExecutorRule()

    protected lateinit var room: ROOM
    protected val dao: DAO by lazy { daoProvider(room) }
    override fun getAllMocks(): Array<Any> = emptyArray()

    override fun setUp() {
        room = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().context,
            roomClass
        )
            .allowMainThreadQueries()
            .build()
    }

    override fun tearDown() {
        room.close()
    }
}
