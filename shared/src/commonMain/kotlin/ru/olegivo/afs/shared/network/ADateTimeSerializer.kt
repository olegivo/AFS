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

package ru.olegivo.afs.shared.network

import kotlinx.datetime.Instant
import kotlinx.datetime.toInstant
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import ru.olegivo.afs.shared.datetime.ADate
import kotlin.time.ExperimentalTime

@ExperimentalSerializationApi
@Serializer(forClass = ADate::class)
object ADateTimeSerializer : KSerializer<ADate> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("ADate", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: ADate) =
        encoder.encodeString(value.toString())

    @ExperimentalTime
    override fun deserialize(decoder: Decoder): ADate {
        val decodeString = decoder.decodeString()
        return ADate.fromString(decodeString)
    }

//    fun fromString(string: String): LocalDateTime {
//        val tzCurrent = TimeZone.currentSystemDefault()
//        with(tzCurrent) {
//            Instant.parse(string).toLocalDateTime()
//        }
//
//        val dateTimeString = string.substring(0, 19) + "Z"
//        val zoneString = string.substring(19)
//        //        dateTimeString.toLocalDateTime()
//        val instant = dateTimeString.toInstant()
//        val timeZone = TimeZone.of(zoneString)
//        val zoneOffset = instant.offsetIn(timeZone)
//        val localDateTime = dateTimeString.toLocalDateTime()
////        if(loc)
//        return localDateTime
//        return instant.toLocalDateTime(timeZone)
//    }

}

@Serializer(forClass = Instant::class)
object InstantSerializer : KSerializer<Instant> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Instant", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Instant) =
        encoder.encodeString(value.toString())

    override fun deserialize(decoder: Decoder): Instant =
        decoder.decodeString().toInstant()

}
