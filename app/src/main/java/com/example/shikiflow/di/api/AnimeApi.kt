package com.example.shikiflow.di.api

import com.example.shikiflow.data.anime.SimilarAnime
import retrofit2.http.GET
import retrofit2.http.Path

interface AnimeApi {
    @GET("/api/animes/{id}/similar")
    suspend fun getSimilarAnime(
        @Path("id") id: String
    ): List<SimilarAnime>
}