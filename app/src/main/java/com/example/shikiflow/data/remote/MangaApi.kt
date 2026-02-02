package com.example.shikiflow.data.remote

import com.example.shikiflow.data.datasource.dto.ShikiManga
import com.example.shikiflow.data.datasource.dto.ExternalLink
import retrofit2.http.GET
import retrofit2.http.Path

interface MangaApi {
    @GET("/api/mangas/{id}/similar")
    suspend fun getSimilarManga(
        @Path("id") id: String
    ): List<ShikiManga>

    @GET("/api/mangas/{id}/external_links")
    suspend fun getExternalLinks(
        @Path("id") id: String
    ): List<ExternalLink>
}