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

package ru.olegivo.afs.shared

import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import ru.olegivo.afs.shared.datetime.ADate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.ExperimentalTime

@ExperimentalTime
class ADateTest {

    @Test
    fun fromString() {
        "2020-01-01T00:00:00+03:00".assertDate(
            year = 2020,
            month = Month.JANUARY,
            day = 1,
            hours = 0,
            minutes = 0,
            seconds = 0,
            timeZone = "+3"
        )
    }

    private fun String.assertDate(
        year: Int, month: Month, day: Int, hours: Int, minutes: Int, seconds: Int, timeZone: String
    ) {
        val aDate = ADate.fromString(this)
        val localDateTime = aDate.local

        assertEquals(year, localDateTime.year, "YEAR")
        assertEquals(month, localDateTime.month, "MONTH")
        assertEquals(day, localDateTime.dayOfMonth, "DAY_OF_MONTH")
        assertEquals(hours, localDateTime.hour, "HOUR_OF_DAY")
        assertEquals(minutes, localDateTime.minute, "MINUTE")
        assertEquals(seconds, localDateTime.second, "SECOND")
        assertEquals(aDate.timeZone, TimeZone.of(timeZone), "TIMEZONE")
    }
}