package ru.olegivo.afs.common.network

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.QueryMap
import ru.olegivo.afs.clubs.domain.models.Club
import ru.olegivo.afs.schedule.network.models.Schedules

interface Api {
    @GET("api/v6/franchise/clubs.json")
    fun getClubs(): Single<List<Club>>

    @GET("api/v6/club/{clubId}/schedule.json")
    fun getSchedule(@Path("clubId") clubId: Int): Single<Schedules>

    @GET("{path}") // for next or prev schedule
    fun getSchedule(@Path("path") path: String, @QueryMap options: Map<String, String>): Single<Schedules>
}
