package ru.olegivo.afs.network

import io.reactivex.Single
import retrofit2.http.GET
import ru.olegivo.afs.domain.clubs.models.Club

interface Api {
    @GET("api/v6/franchise/clubs.json")
    fun getClubs(): Single<List<Club>>
}
