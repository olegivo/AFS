package ru.olegivo.afs.common.network

import io.reactivex.Single
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap
import ru.olegivo.afs.clubs.domain.models.Club
import ru.olegivo.afs.schedule.network.models.Schedules
import ru.olegivo.afs.schedule.network.models.Slot

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
}
