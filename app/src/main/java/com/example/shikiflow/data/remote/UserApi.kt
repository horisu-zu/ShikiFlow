package com.example.shikiflow.data.remote

import com.example.shikiflow.data.datasource.dto.ShikiShortUserRateResponse
import com.example.shikiflow.data.datasource.dto.ShikiCreateRateRequest
import com.example.shikiflow.data.datasource.dto.ShikiHistoryResponse
import com.example.shikiflow.data.datasource.dto.ShikiUpdateRateRequest
import com.example.shikiflow.data.datasource.dto.ShikiUserRateResponse
import com.example.shikiflow.data.datasource.dto.ShikiUserFavoritesResponse
import com.example.shikiflow.data.datasource.dto.media.ShikiShortUserRate
import com.example.shikiflow.data.datasource.dto.comment.ShikiUser
import retrofit2.http.Body
import retrofit2.http.DELETE
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
    ): List<ShikiHistoryResponse>

    @GET("/api/users/{userId}/anime_rates")
    suspend fun getShortUserAnimeRates(
        @Path("userId") userId: Long,
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int = 5000,
        @Query("status") status: String? = null,
        @Query("censored") censored: Boolean? = true
    ): List<ShikiShortUserRate.ShikiShortAnimeRate>

    @GET("/api/users/{userId}/manga_rates")
    suspend fun getShortUserMangaRates(
        @Path("userId") userId: Long,
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int = 5000,
        @Query("status") status: String? = null,
        @Query("censored") censored: Boolean? = true
    ): List<ShikiShortUserRate.ShikiShortMangaRate>

    @GET("/api/v2/user_rates")
    suspend fun getUserRates(
        @Query("user_id") userId: Long,
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null,
        @Query("status") status: String? = null,
        @Query("target_type") targetType: String? = null,
        @Query("censored") censored: Boolean? = true
    ): List<ShikiShortUserRateResponse>

    @PATCH("/api/v2/user_rates/{id}")
    suspend fun updateUserRate(
        @Path("id") id: Long,
        @Body request: ShikiUpdateRateRequest
    ): ShikiUserRateResponse

    @POST("api/v2/user_rates")
    suspend fun createUserRate(
        @Body createRequest: ShikiCreateRateRequest
    ): ShikiUserRateResponse

    @DELETE("api/v2/user_rates/{id}")
    suspend fun deleteUserRate(
        @Path("id") id: Long
    )

    @GET("/api/users/{id}/favourites")
    suspend fun getUserFavorites(
        @Path("id") userId: Long
    ): ShikiUserFavoritesResponse

    @GET("/api/users/{id}/friends")
    suspend fun getUserFriends(
        @Path("id") userId: Long,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 1
    ): List<ShikiUser>
}