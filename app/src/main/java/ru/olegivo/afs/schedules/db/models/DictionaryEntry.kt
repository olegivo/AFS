package ru.olegivo.afs.schedules.db.models

import androidx.room.Entity

@Entity(
    tableName = "dictionary",
    primaryKeys = ["dictionaryId", "key"]
)
data class DictionaryEntry(val dictionaryId: Int, val key: Int, val value: String)

inline class DictionaryKind(val value: Int) {
    companion object {
        val Group = DictionaryKind(1)
        val Activity = DictionaryKind(1)
    }
}
