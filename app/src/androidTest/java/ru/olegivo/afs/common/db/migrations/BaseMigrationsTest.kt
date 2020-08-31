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

package ru.olegivo.afs.common.db.migrations

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Rule
import ru.olegivo.afs.common.db.AfsDatabase

abstract class BaseMigrationsTest(
    private val migration: Migration,
    private val versionFrom: Int,
    private val versionTo: Int
) {
    protected lateinit var db: SupportSQLiteDatabase

    @Rule
    @JvmField
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AfsDatabase::class.java.canonicalName,
        FrameworkSQLiteOpenHelperFactory()
    )

    @After
    fun tearDown() {
        closeDb()
    }

    protected fun closeDb() {
        if (db.isOpen) db.close()
    }

    protected fun prepareOld(prepareAction: () -> Unit) {
        db = helper.createDatabase(TEST_DB_NAME, versionFrom)
        prepareAction()
        closeDb()
    }

    protected fun insert(tableName: String, values: ContentValues) =
        db.insert(tableName, SQLiteDatabase.CONFLICT_FAIL, values)

    protected fun <R> query(query: String, fetch: Cursor.() -> R) =
        db.query(query).use(fetch)

    protected fun migrate() {
        db = helper.runMigrationsAndValidate(TEST_DB_NAME, versionTo, true, migration)
    }

    protected fun getRoomInstance(): AfsDatabase {
        return Room
            .databaseBuilder(
                InstrumentationRegistry.getInstrumentation().targetContext,
                AfsDatabase::class.java,
                TEST_DB_NAME
            )
            .build()
    }

    companion object {
        private const val TEST_DB_NAME = "test-db"
    }
}
