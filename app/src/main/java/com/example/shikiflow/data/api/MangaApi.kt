package com.example.shikiflow.data.api

import com.example.shikiflow.data.anime.ShikiManga
import com.example.shikiflow.data.common.ExternalLink
import retrofit2.http.GET
import retrofit2.http.Path

interface MangaApi {
    @GET("/api/mangas/{id}/similar")
    suspend fun getSimilarMangas(
        @Path("id") id: String
    ): List<ShikiManga>

    @GET("/api/mangas/{id}/external_links")
    suspend fun getExternalLinks(
        @Path("id") id: String
    ): List<ExternalLink>
}