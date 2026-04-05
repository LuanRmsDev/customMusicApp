package dev.luanramos.custommusicapp.data.remote.itunes

import retrofit2.http.GET
import retrofit2.http.Query

interface ItunesSearchApi {

    @GET("search")
    suspend fun search(
        @Query("term") term: String,
        @Query("media") media: String = "music",
        @Query("limit") limit: Int,
        @Query("offset") offset: Int = 0,
    ): ItunesSearchResponseDto
}
