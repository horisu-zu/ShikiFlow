package com.example.shikiflow.di.api

import com.example.shikiflow.data.anime.ShikiManga
import retrofit2.http.GET
import retrofit2.http.Path

interface MangaApi {
    @GET("/api/mangas/{id}/similar")
    suspend fun getSimilarMangas(
        @Path("id") id: String
    ): List<ShikiManga>
}