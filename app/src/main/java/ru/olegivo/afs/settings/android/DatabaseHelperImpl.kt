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

package ru.olegivo.afs.settings.android

import android.content.Context
import com.squareup.sqldelight.db.SqlDriver
import io.reactivex.Completable
import io.reactivex.Scheduler
import ru.olegivo.afs.BuildConfig
import ru.olegivo.afs.settings.domain.DatabaseHelper
import java.io.File
import javax.inject.Inject
import javax.inject.Named

class DatabaseHelperImpl @Inject constructor(
    private val sqlDriver: SqlDriver,
    @Named("application") private val context: Context,
    @Named("io") private val ioScheduler: Scheduler
) : DatabaseHelper {

    override fun delete(): Completable {
        return Completable.fromCallable {
            sqlDriver.close()
            deleteDatabaseFile(context, BuildConfig.DB_NAME)
        }.subscribeOn(ioScheduler)
    }

    private fun deleteDatabaseFile(context: Context, databaseName: String): Boolean {
        val databasePath = File(context.applicationInfo.dataDir + "/databases")
        return delete(File(databasePath, databaseName)) &&
            delete(File(databasePath, "$databaseName-journal"))
    }

    private fun delete(file: File): Boolean {
        if (file.exists()) {
            if (file.delete()) {
                println("${file.name} deleted")
            } else {
                println("Failed to delete ${file.name}")
                return false
            }
        }
        return true
    }
}
