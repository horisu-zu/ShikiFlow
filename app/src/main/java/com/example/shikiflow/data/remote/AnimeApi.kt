package com.example.shikiflow.data.remote

import com.example.shikiflow.data.datasource.dto.CalendarAnime
import com.example.shikiflow.data.datasource.dto.ExternalLink
import com.example.shikiflow.data.datasource.dto.ShikiAnime
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface AnimeApi {
    @GET("/api/animes/{id}/similar")
    suspend fun getSimilarAnime(
        @Path("id") id: String
    ): List<ShikiAnime>

    @GET("/api/animes/{id}/external_links")
    suspend fun getExternalLinks(
        @Path("id") id: String
    ): List<ExternalLink>

    @GET("api/calendar")
    suspend fun getOngoingsCalendar(
        @Query("censored") censored: Boolean = true
    ): List<CalendarAnime>
}