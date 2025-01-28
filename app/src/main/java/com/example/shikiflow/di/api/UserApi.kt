package com.example.shikiflow.di.api

import com.example.shikiflow.data.anime.ShortAnimeRate
import com.example.shikiflow.data.user.UserHistoryResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface UserApi {
    @GET("/api/users/{userId}/history")
    suspend fun getUserHistory(
        @Path("userId") userId: Long,
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null,
        @Query("target_id") targetId: Long? = null,
        @Query("target_type") targetType: String? = null
    ): List<UserHistoryResponse>

    @GET("/api/users/{userId}/anime_rates")
    suspend fun getUserAnimeRates(
        @Path("userId") userId: Long,
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null,
        @Query("status") status: String? = null,
        @Query("censored") censored: Boolean? = true
    ): List<ShortAnimeRate>
}