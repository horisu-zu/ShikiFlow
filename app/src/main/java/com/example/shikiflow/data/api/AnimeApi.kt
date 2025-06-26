package com.example.shikiflow.data.api

import com.example.shikiflow.data.anime.SimilarAnime
import com.example.shikiflow.data.common.ExternalLink
import retrofit2.http.GET
import retrofit2.http.Path

interface AnimeApi {
    @GET("/api/animes/{id}/similar")
    suspend fun getSimilarAnime(
        @Path("id") id: String
    ): List<SimilarAnime>

    @GET("/api/animes/{id}/external_links")
    suspend fun getExternalLinks(
        @Path("id") id: String
    ): List<ExternalLink>
}