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

package ru.olegivo.afs.schedules.db.models

import androidx.room.Entity

@Entity(
    tableName = "dictionary",
    primaryKeys = ["dictionaryId", "key"]
)
data class DictionaryEntry(val dictionaryId: Int, val key: Int, val value: String)

inline class DictionaryKind(val value: Int) {
    companion object {
        const val GROUP_ID = 1
        const val ACTIVITY_ID = 2

        val Group = DictionaryKind(GROUP_ID)
        val Activity = DictionaryKind(ACTIVITY_ID)
    }
}
