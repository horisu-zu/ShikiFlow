package com.example.shikiflow.di.api

import com.example.shikiflow.data.anime.ShortAnimeRate
import com.example.shikiflow.data.manga.ShortMangaRate
import com.example.shikiflow.data.tracks.CreateUserRateRequest
import com.example.shikiflow.data.user.UserHistoryResponse
import com.example.shikiflow.data.tracks.UserRate
import com.example.shikiflow.data.tracks.UserRateRequest
import com.example.shikiflow.data.tracks.UserRateResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
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

    @GET("/api/users/{userId}/manga_rates")
    suspend fun getUserMangaRates(
        @Path("userId") userId: Long,
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null,
        @Query("status") status: String? = null,
        @Query("censored") censored: Boolean? = true
    ): List<ShortMangaRate>

    @GET("/api/v2/user_rates")
    suspend fun getUserRates(
        @Query("user_id") userId: Long,
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null,
        @Query("status") status: String? = null,
        @Query("target_type") targetType: String? = null,
        @Query("censored") censored: Boolean? = true
    ): List<UserRate>

    @PATCH("/api/v2/user_rates/{id}")
    suspend fun updateUserRate(
        @Path("id") id: Long,
        @Body request: UserRateRequest
    ): UserRateResponse

    @POST("api/v2/user_rates")
    suspend fun createUserRate(
        @Body createRequest: CreateUserRateRequest
    ): UserRateResponse
}