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

package ru.olegivo.afs.shared.datetime

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.offsetAt
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.hours
import kotlin.time.minutes

data class ADate(val local: LocalDateTime, val timeZone: TimeZone) {
    companion object {
        @ExperimentalTime
        fun fromString(string: String): ADate {
            val zoneString = string.substring(19)
            val timeZone = TimeZone.of(zoneString)
            val dateTimeString = string.substring(0, 19) + "Z"
            val instant = Instant.parse(dateTimeString)
                .let { inst ->
                    val sign = zoneString.first().let {
                        when (it) {
                            '+' -> true
                            '-' -> false
                            else -> null
                        }
                    }
                    val timeOffset = zoneString.substring(sign?.let { 1 } ?: 0).split(':')
                    val offsetHours = timeOffset.firstOrNull()?.toInt()?.hours
                        ?: Duration.ZERO
                    val offsetMinutes = timeOffset.drop(1).firstOrNull()?.toInt()?.minutes
                        ?: Duration.ZERO
                    val offset =
                        (offsetHours + offsetMinutes).let { if (sign == false) it else -it }
                    inst + offset
                }

            with(timeZone) {
                val localDateTimeUtc = instant.toLocalDateTime()
                return ADate(localDateTimeUtc, timeZone)
            }
        }
    }
}