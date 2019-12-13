package ru.olegivo.afs.common.network

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import org.joda.time.DateTime
import java.io.IOException
import java.util.*

class DateJsonAdapter : JsonAdapter<Date>() {

    @Synchronized
    @Throws(IOException::class)
    override fun fromJson(reader: JsonReader): Date {
        val timeStamp = reader.nextString()
        return DateTime(timeStamp).toDate()
    }

    @Synchronized
    @Throws(IOException::class)
    override fun toJson(writer: JsonWriter, value: Date?) {
        value?.let { writer.value(it.toString()) }
    }
}
