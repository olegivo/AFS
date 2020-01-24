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
