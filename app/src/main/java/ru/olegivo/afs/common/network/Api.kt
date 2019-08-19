package ru.olegivo.afs.common.network

import io.reactivex.Single
import retrofit2.http.GET
import ru.olegivo.afs.clubs.domain.models.Club

interface Api {
    @GET("api/v6/franchise/clubs.json")
    fun getClubs(): Single<List<Club>>
}
