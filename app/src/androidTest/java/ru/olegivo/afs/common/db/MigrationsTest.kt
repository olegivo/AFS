package ru.olegivo.afs.common.db

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import androidx.room.migration.Migration
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule

class MigrationsTest {
    private lateinit var db: SupportSQLiteDatabase

    @Rule
    @JvmField
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AfsDatabase::class.java.canonicalName,
        FrameworkSQLiteOpenHelperFactory()
    )

    private fun insert(tableName: String, values: ContentValues) =
        db.insert(tableName, SQLiteDatabase.CONFLICT_FAIL, values)

    private fun prepareOld(version: Int, prepareAction: () -> Unit) {
        db = helper.createDatabase(TEST_DB, version)
        prepareAction()
        db.close()
    }

    private fun migrate(version: Int, migration: Migration) {
        val nextVersion = version + 1
        db = helper.runMigrationsAndValidate(TEST_DB, nextVersion, true, migration)
    }

    companion object {
        private const val TEST_DB = "test-db"
    }
}