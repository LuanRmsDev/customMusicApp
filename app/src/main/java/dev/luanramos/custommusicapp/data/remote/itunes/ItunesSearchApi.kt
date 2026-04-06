package dev.luanramos.custommusicapp.data.remote.itunes

import retrofit2.http.GET
import retrofit2.http.Query

interface ItunesSearchApi {

    @GET("search")
    suspend fun search(
        @Query("term") term: String,
        @Query("media") media: String = "music",
        @Query("entity") entity: String = "song",
        @Query("limit") limit: Int,
        @Query("offset") offset: Int = 0,
    ): ItunesSearchResponseDto

   @GET("lookup")
    suspend fun lookupByAmgAlbumId(
        @Query("amgAlbumId") amgAlbumId: Long,
        @Query("entity") entity: String = "song",
    ): ItunesSearchResponseDto

    @GET("lookup")
    suspend fun lookupByCollectionId(
        @Query("id") collectionId: Long,
        @Query("entity") entity: String = "song",
    ): ItunesSearchResponseDto
}
