package com.example.shikiflow.data.api

import com.example.shikiflow.data.mangadex.aggregate.AggregateResponse
import com.example.shikiflow.data.mangadex.manga.MangaDexResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MangaDexApi {

    @GET("/manga")
    suspend fun getMangaList(
        @Query("title") title: String
    ): MangaDexResponse

    @GET("/manga/{id}/aggregate")
    suspend fun aggregateManga(
        @Path("id") mangaId: String
    ): AggregateResponse
}