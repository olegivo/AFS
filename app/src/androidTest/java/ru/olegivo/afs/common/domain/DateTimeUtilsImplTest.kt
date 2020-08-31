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

package ru.olegivo.afs.common.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import ru.olegivo.afs.BaseTestOf
import ru.olegivo.afs.common.add
import ru.olegivo.afs.common.firstDayOfWeek
import java.util.Date

class DateTimeUtilsImplTest : BaseTestOf<DateTimeUtils>() {
    override fun createInstance() = DateTimeUtilsImpl()

    override fun getAllMocks(): Array<Any> = emptyArray()

    @Test
    fun getWeekDayNumber_returns_1_when_1st_day_of_current_week() {
        val firstDayOfWeek = firstDayOfWeek(Date())
        assertThat(instance.getWeekDayNumber(firstDayOfWeek)).isEqualTo(1)
    }

    @Test
    fun getWeekDayNumber_returns_2_when_2nd_day_of_current_week() {
        val firstDayOfWeek = firstDayOfWeek(Date())
        assertThat(instance.getWeekDayNumber(firstDayOfWeek.add(days = 1))).isEqualTo(2)
    }

    @Test
    fun getWeekDayNumber_returns_7_when_7th_day_of_current_week() {
        val firstDayOfWeek = firstDayOfWeek(Date())
        assertThat(instance.getWeekDayNumber(firstDayOfWeek.add(days = 6))).isEqualTo(7)
    }

    @Test
    fun getWeekDayNumber_returns_1_when_1st_day_of_next_week() {
        val firstDayOfWeek = firstDayOfWeek(Date())
        assertThat(instance.getWeekDayNumber(firstDayOfWeek.add(days = 7))).isEqualTo(1)
    }

    @Test
    fun getWeekDayNumber_returns_7_when_7th_day_of_prev_week() {
        val firstDayOfWeek = firstDayOfWeek(Date())
        assertThat(instance.getWeekDayNumber(firstDayOfWeek.add(days = -1))).isEqualTo(7)
    }
}
