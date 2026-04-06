package dev.luanramos.custommusicapp.data.remote.itunes

import retrofit2.http.GET
import retrofit2.http.Query

interface ItunesSearchApi {

    /** Apple's Search API documents [limit] in the range 1–200 (see repository for the value used in production). */
    @GET("search")
    suspend fun search(
        @Query("term") term: String,
        @Query("media") media: String = "music",
        @Query("entity") entity: String = "song",
        @Query("limit") limit: Int,
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
