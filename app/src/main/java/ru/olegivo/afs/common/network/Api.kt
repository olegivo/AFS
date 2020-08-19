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

import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap
import ru.olegivo.afs.schedule.network.models.ReserveRequest
import ru.olegivo.afs.schedules.network.models.Club
import ru.olegivo.afs.schedules.network.models.Schedules
import ru.olegivo.afs.schedules.network.models.Slot

interface Api {
    @GET("api/v6/franchise/clubs.json")
    fun getClubs(): Single<List<Club>>

    @GET("api/v6/club/{clubId}/schedule.json")
    fun getSchedule(@Path("clubId") clubId: Int): Single<Schedules>

    @GET("{path}") // for next or prev schedule
    fun getSchedule(@Path("path") path: String, @QueryMap options: Map<String, String>): Single<Schedules>

    @FormUrlEncoded
    @POST("api/v6/schedule/chain/slots.json")
    fun getSlots(@Query("clubId") clubId: Int, @FieldMap idByPosition: Map<String, String>): Single<List<Slot>>

    @POST("api/v6/account/reserve.json")
    @Headers("Accept: application/json", "Content-Type: application/json")
    fun reserve(@Body reserveRequest: ReserveRequest): Completable
}
