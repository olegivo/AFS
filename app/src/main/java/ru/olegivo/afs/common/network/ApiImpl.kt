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

package ru.olegivo.afs.common.network

import io.ktor.client.HttpClient
import io.ktor.client.request.accept
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.takeFrom
import ru.olegivo.afs.BuildConfig
import ru.olegivo.afs.schedule.network.models.ReserveRequest
import ru.olegivo.afs.schedules.network.models.Club
import ru.olegivo.afs.schedules.network.models.Schedules
import ru.olegivo.afs.schedules.network.models.Slot
import javax.inject.Inject

class ApiImpl @Inject constructor(
    private val httpClient: HttpClient
) : Api {

    private val apiUrl = BuildConfig.API_URL

    override suspend fun getClubs(): List<Club> =
        httpClient.get {
            url {
                takeFrom("${apiUrl}api/v6/franchise/clubs.json")
            }
        }

    override suspend fun getSchedule(clubId: Int): Schedules =
        httpClient.get {
            url {
                takeFrom("${apiUrl}api/v6/club/$clubId/schedule.json")
            }
        }

    override suspend fun getSchedule(path: String, options: Map<String, String>): Schedules =
        httpClient.get {
            url {
                takeFrom("${apiUrl}$path")
                options.forEach {
                    parameter(it.key, listOf(it.value))
                }
            }
        }

    override suspend fun getSlots(clubId: Int, idByPosition: Map<String, String>): List<Slot> =
        httpClient.post {
            url {
                takeFrom("${apiUrl}api/v6/schedule/chain/slots.json")
                parameter("clubId", clubId)
                formData {
                    idByPosition.forEach { (key, value) ->
                        append(key, value)
                    }
                }
            }
        }

    override suspend fun reserve(reserveRequest: ReserveRequest): Unit =
        httpClient.post {
            url {
                takeFrom("${apiUrl}api/v6/account/reserve.json")
                accept(ContentType.Application.Json)
                contentType(ContentType.Application.Json)
                body = reserveRequest
            }
        }
}
