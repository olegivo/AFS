package ru.olegivo.afs.common.network

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class DateJsonAdapter : JsonAdapter<Date>() {

    @Synchronized
    @Throws(IOException::class)
    override fun fromJson(reader: JsonReader): Date {
        val timeStamp = reader.nextString()
        return Date(simpleDateFormat.parse(timeStamp).time + gmtOffset)
    }

    @Synchronized
    @Throws(IOException::class)
    override fun toJson(writer: JsonWriter, value: Date?) {
        val string = getUTCDate(value)
        writer.value(string)
    }

    companion object {
        private const val FORMAT = "yyyy-MM-dd'T'HH:mm:ss"

        private val simpleDateFormat: SimpleDateFormat by lazy {
            SimpleDateFormat(
                FORMAT,
                Locale.getDefault()
            )
        }
        private val gmtOffset: Int by lazy { TimeZone.getDefault().rawOffset }

        private fun getUTCDate(value: Date?): String? {
            if (value == null) return null
            val gmtOffset = TimeZone.getDefault().rawOffset
            val format = simpleDateFormat
            return format.format(Date(value.time - gmtOffset))
            //        SimpleDateFormat format = new SimpleDateFormat(FORMAT);
            //        return format.format(new Date(value.getTime()));
        }

        @Throws(ParseException::class)
        fun parse(date: String): Date {
            val format = simpleDateFormat
            return format.parse(date)
        }
    }
}
