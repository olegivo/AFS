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

package ru.olegivo.afs.common

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.util.Calendar

class DateExKtTest {
    @Test
    fun `Date_add return 2001-05-01 WHEN 2000-05-01 +1 year`() {
        val cal = Calendar.getInstance()
        cal.set(2000, 5, 1)
        val date = cal.time

        cal.time = date.add(years = 1)

        assertThat(cal[Calendar.YEAR]).isEqualTo(2001)
        assertThat(cal[Calendar.MONTH]).isEqualTo(5)
        assertThat(cal[Calendar.DAY_OF_MONTH]).isEqualTo(1)
    }

    @Test
    fun `Date_add return 1999-05-01 WHEN 2000-05-01 -1 year`() {
        val cal = Calendar.getInstance()
        cal.set(2000, 5, 1)
        val date = cal.time

        cal.time = date.add(years = -1)

        assertThat(cal[Calendar.YEAR]).isEqualTo(1999)
        assertThat(cal[Calendar.MONTH]).isEqualTo(5)
        assertThat(cal[Calendar.DAY_OF_MONTH]).isEqualTo(1)
    }

    @Test
    fun `Date_add return 2001-03-01 WHEN 2000-05-01 +10 months`() {
        val cal = Calendar.getInstance()
        cal.set(2000, 5, 1)
        val date = cal.time

        cal.time = date.add(months = 10)

        assertThat(cal[Calendar.YEAR]).isEqualTo(2001)
        assertThat(cal[Calendar.MONTH]).isEqualTo(3)
        assertThat(cal[Calendar.DAY_OF_MONTH]).isEqualTo(1)
    }

    @Test
    fun `Date_add return 1999-12-01 WHEN 2000-05-01 -10 months`() {
        val cal = Calendar.getInstance()
        cal.set(2000, 5, 1)
        val date = cal.time

        cal.time = date.add(months = -10)

        assertThat(cal[Calendar.YEAR]).isEqualTo(1999)
        assertThat(cal[Calendar.MONTH]).isEqualTo(7)
        assertThat(cal[Calendar.DAY_OF_MONTH]).isEqualTo(1)
    }

    @Test
    fun `Date_add return 2001-06-05 WHEN 2000-05-15 +20 days`() {
        val cal = Calendar.getInstance()
        cal.set(2000, 5, 15)
        val date = cal.time

        cal.time = date.add(days = 20)

        assertThat(cal[Calendar.YEAR]).isEqualTo(2000)
        assertThat(cal[Calendar.MONTH]).isEqualTo(6)
        assertThat(cal[Calendar.DAY_OF_MONTH]).isEqualTo(5)
    }

    @Test
    fun `Date_add return 2000-04-26 WHEN 2000-05-15 -20 days`() {
        val cal = Calendar.getInstance()
        cal.set(2000, 5, 15)
        val date = cal.time

        cal.time = date.add(days = -20)

        assertThat(cal[Calendar.YEAR]).isEqualTo(2000)
        assertThat(cal[Calendar.MONTH]).isEqualTo(4)
        assertThat(cal[Calendar.DAY_OF_MONTH]).isEqualTo(26)
    }
}
