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

package ru.olegivo.afs.schedules.network.models

import org.assertj.core.api.SoftAssertions.assertSoftly
import org.junit.Ignore
import org.junit.Test
import ru.olegivo.afs.common.get
import ru.olegivo.afs.shared.datetime.ADate
import java.util.Calendar
import kotlin.time.ExperimentalTime

@ExperimentalTime
class MappersKtTest {
    @Test
    @Ignore("test cannot run out of +3 time zone")
    fun `source - first day of the year +3, target +3`() {
        "2020-01-01T00:00:00+03:00".assertDate(
            year = 2020,
            month = Calendar.JANUARY,
            day = 1,
            hours = 0,
            minutes = 0,
            seconds = 0
        )
    }

    @Test
    @Ignore("test cannot run out of +3 time zone")
    fun `source - last moment of the year +3, target +3`() {
        "2019-12-31T23:59:59+03:00".assertDate(
            year = 2019,
            month = Calendar.DECEMBER,
            day = 31,
            hours = 23,
            minutes = 59,
            seconds = 59
        )
    }

    @Test
    @Ignore("test cannot run out of +3 time zone")
    fun `source - first day of the year +2, target +3`() {
        "2020-01-01T00:00:00+02:00".assertDate(
            year = 2020,
            month = Calendar.JANUARY,
            day = 1,
            hours = 1,
            minutes = 0,
            seconds = 0
        )
    }

    @Test
    @Ignore("test cannot run out of +3 time zone")
    fun `source - last moment of the year +2, target +3`() {
        "2019-12-31T23:59:59+02:00"
            .assertDate(
                year = 2020,
                month = Calendar.JANUARY,
                day = 1,
                hours = 0,
                minutes = 59,
                seconds = 59
            )
    }

    private fun String.assertDate(
        year: Int,
        month: Int,
        day: Int,
        hours: Int,
        minutes: Int,
        seconds: Int
    ) {
        val aDate = ADate.fromString(this)
        val toDate = aDate.toDate()
        val descr = buildString {
            appendLine("origin: ${this@assertDate}")
            appendLine("aDate: $aDate")
            appendLine("result: $toDate")
        }
        assertSoftly {
            it.assertThat(toDate.get(Calendar.YEAR)).describedAs("YEAR\n$descr")
                .isEqualTo(year)
            it.assertThat(toDate.get(Calendar.MONTH)).describedAs("MONTH\n$descr")
                .isEqualTo(month)
            it.assertThat(toDate.get(Calendar.DAY_OF_MONTH)).describedAs("DAY_OF_MONTH\n$descr")
                .isEqualTo(day)
            it.assertThat(toDate.get(Calendar.HOUR_OF_DAY)).describedAs("HOUR_OF_DAY\n$descr")
                .isEqualTo(hours)
            it.assertThat(toDate.get(Calendar.MINUTE)).describedAs("MINUTE\n$descr")
                .isEqualTo(minutes)
            it.assertThat(toDate.get(Calendar.SECOND)).describedAs("SECOND\n$descr")
                .isEqualTo(seconds)
        }
    }
}
