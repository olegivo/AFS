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

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import ru.olegivo.afs.shared.datetime.ADate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Date

fun LocalDateTime.toDate(): Date {
    return Date(toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds())
}

fun ADate.toDate(): Date {
    with(timeZone) {
        return Date(local.toInstant().toEpochMilliseconds())
    }
}

fun Date.toADate() = TimeZone.currentSystemDefault().let {
    with(it) {
        ADate(Instant.fromEpochMilliseconds(time).toLocalDateTime(), it)
    }
}

fun Instant.toDate(): Date {
    return Date(toEpochMilliseconds())
}

fun Date.toInstant() = Instant.fromEpochMilliseconds(time)

fun String.toDate(): Date {
    val offsetDateTime = OffsetDateTime.parse(this, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    val instant = offsetDateTime.toInstant()
    return Date(instant.toEpochMilli())
}
