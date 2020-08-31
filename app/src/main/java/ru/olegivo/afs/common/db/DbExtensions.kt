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

import androidx.core.database.getIntOrNull
import androidx.sqlite.db.SupportSQLiteDatabase

fun SupportSQLiteDatabase.doInTransaction(action: SupportSQLiteDatabase.() -> Unit) {
    beginTransaction()
    try {
        action()
        setTransactionSuccessful()
    } finally {
        endTransaction()
    }
}

fun SupportSQLiteDatabase.getCount(query: String) =
    query(query).use {
        if (it.moveToFirst()) {
            it.getIntOrNull(0) ?: 0
        } else {
            0
        }
    }

fun SupportSQLiteDatabase.exists(query: String) =
    query(
        buildString {
            appendLine("SELECT EXISTS (")
            appendLine(query)
            appendLine(")")
        }
    ).use {
        if (it.moveToFirst()) {
            it.getIntOrNull(0) ?: 0
        } else {
            0
        } != 0
    }
