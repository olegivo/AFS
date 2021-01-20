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

import retrofit2.http.FormUrlEncoded
import retrofit2.http.QueryMap
import ru.olegivo.afs.schedule.network.models.ReserveRequest
import ru.olegivo.afs.shared.network.NewApi
import ru.olegivo.afs.shared.network.models.Schedules
import ru.olegivo.afs.shared.network.models.Slot

interface Api : NewApi {

    suspend fun getSchedule(clubId: Int): Schedules

    // for next or prev schedule
    suspend fun getSchedule(path: String, @QueryMap options: Map<String, String>): Schedules

    @FormUrlEncoded
    suspend fun getSlots(clubId: Int, idByPosition: Map<String, String>): List<Slot>

    suspend fun reserve(reserveRequest: ReserveRequest)
}
