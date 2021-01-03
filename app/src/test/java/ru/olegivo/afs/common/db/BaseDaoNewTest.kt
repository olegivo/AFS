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

package ru.olegivo.afs.common.db

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import io.reactivex.schedulers.TestScheduler
import ru.olegivo.afs.BaseTest
import ru.olegivo.afs.common.di.DbModuleCore

abstract class BaseDaoNewTest<DAO : Any>(private val daoProvider: (AfsDatabaseNew, TestScheduler) -> DAO) :
    BaseTest() {

    private lateinit var sqliteDriver: SqlDriver
    private lateinit var db: AfsDatabaseNew
    protected lateinit var dao: DAO

    override fun getAllMocks(): Array<Any> = arrayOf()

    override fun setUp() {
        sqliteDriver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        AfsDatabaseNew.Schema.create(driver = sqliteDriver)
        db = DbModuleCore.providesAfsDatabaseNew(sqliteDriver)
        dao = daoProvider(db, testScheduler)
    }

    override fun tearDown() {
        super.tearDown()
        sqliteDriver.close()
    }
}
