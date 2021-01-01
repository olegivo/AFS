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

package ru.olegivo.afs.common

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Suppress("LongParameterList")
fun Date.add(
    years: Int? = null,
    months: Int? = null,
    days: Int? = null,
    hours: Int? = null,
    minutes: Int? = null,
    seconds: Int? = null,
    milliseconds: Int? = null
): Date =
    toCalendar().add(years, months, days, hours, minutes, seconds, milliseconds).time

@Suppress("LongParameterList")
fun Calendar.add(
    years: Int? = null,
    months: Int? = null,
    days: Int? = null,
    hours: Int? = null,
    minutes: Int? = null,
    seconds: Int? = null,
    milliseconds: Int? = null
): Calendar =
    this.apply {
        years?.let { add(Calendar.YEAR, it) }
        months?.let { add(Calendar.MONTH, it) }
        days?.let { add(Calendar.DAY_OF_MONTH, it) }
        hours?.let { add(Calendar.HOUR_OF_DAY, it) }
        minutes?.let { add(Calendar.MINUTE, it) }
        seconds?.let { add(Calendar.SECOND, it) }
        milliseconds?.let { add(Calendar.MILLISECOND, it) }
    }

fun Date.hasCurrentYear(): Boolean =
    Date().toCalendar().get(Calendar.YEAR) == this.toCalendar().get(Calendar.YEAR)

fun Date.get(field: Int) =
    toCalendar().get(field)

@Suppress("LongParameterList")
fun date(
    years: Int? = null,
    months: Int? = null,
    days: Int? = null,
    hours: Int? = null,
    minutes: Int? = null,
    seconds: Int? = null,
    milliseconds: Int? = null
) =
    Date(0L).toCalendar().apply {
        set(Calendar.YEAR, years ?: 0)
        set(Calendar.MONTH, months ?: 0)
        set(Calendar.DAY_OF_MONTH, days ?: 0)
        set(Calendar.HOUR_OF_DAY, hours ?: 0)
        set(Calendar.MINUTE, minutes ?: 0)
        set(Calendar.SECOND, seconds ?: 0)
        set(Calendar.MILLISECOND, milliseconds ?: 0)
    }.time

fun Date.toCalendar() = Calendar.getInstance(Locale.GERMANY).apply { time = this@toCalendar }

fun Date.getDateWithoutTime(): Date {
    val calendar = toCalendar()
    calendar.time = this

    calendar.set(Calendar.HOUR_OF_DAY, 0) // ! clear would not reset the hour of day !
    calendar.clear(Calendar.MINUTE)
    calendar.clear(Calendar.SECOND)
    calendar.clear(Calendar.MILLISECOND)
    return calendar.time
}

fun Date.equalsWithoutTime(date: Date): Boolean {
    val thisDate = toCalendar()
    val anotherDate = date.toCalendar()
    return thisDate.get(Calendar.YEAR) == anotherDate.get(Calendar.YEAR) &&
        thisDate.get(Calendar.MONTH) == anotherDate.get(Calendar.MONTH) &&
        thisDate.get(Calendar.DAY_OF_YEAR) == anotherDate.get(Calendar.DAY_OF_YEAR)
}

fun isDate(dateString: String): Boolean {
    var result = true
    val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    dateFormat.isLenient = false
    try {
        val convertedDate = dateFormat.parse(dateString)
        println(convertedDate)
    } catch (e: ParseException) {
        e.printStackTrace()
        result = false
    }

    return result
}

fun toDate(dateString: String): Date {
    val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    var convertedDate = Date()
    try {
        convertedDate = dateFormat.parse(dateString)
    } catch (e: ParseException) {
        e.printStackTrace()
    }

    return convertedDate
}

fun today() = Date().getDateWithoutTime()

fun yesterday() = today().add(days = -1)

fun tomorrow() = today().add(days = 1)

fun firstDayOfWeek(date: Date = today()): Date =
    // get today and clear time of day
    date.getDateWithoutTime()
        .toCalendar().apply {
            // get start of this week in milliseconds
            set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
        }.time

private const val MINUTES_IN_HOUR = 60

fun Date.getMinutesOfDay(): Int = toCalendar().let {
    it[Calendar.HOUR_OF_DAY] * MINUTES_IN_HOUR + it[Calendar.MINUTE]
}
