package com.example.shikiflow.domain.repository

import com.example.graphql.CurrentUserQuery
import com.example.shikiflow.domain.model.anime.ShortAnimeRate
import com.example.shikiflow.data.manga.ShortMangaRate
import com.example.shikiflow.domain.model.tracks.CreateUserRateRequest
import com.example.shikiflow.domain.model.tracks.TargetType
import com.example.shikiflow.domain.model.user.UserHistoryResponse
import com.example.shikiflow.domain.model.tracks.UserRate
import com.example.shikiflow.domain.model.tracks.UserRateRequest
import com.example.shikiflow.domain.model.tracks.UserRateResponse

interface UserRepository {
    suspend fun fetchCurrentUser(): Result<CurrentUserQuery.Data>
    suspend fun getUserHistory(
        userId: Long,
        page: Int? = null,
        limit: Int? = null,
        targetId: Long? = null,
        targetType: TargetType? = null
    ): Result<List<UserHistoryResponse>>

    suspend fun getUserAnimeRates(
        userId: Long,
        page: Int? = null,
        limit: Int? = null,
        status: String? = null,
        censored: Boolean? = true
    ): Result<List<ShortAnimeRate>>

    suspend fun getUserMangaRates(
        userId: Long,
        page: Int? = null,
        limit: Int? = null,
        status: String? = null,
        censored: Boolean? = true
    ): Result<List<ShortMangaRate>>

    suspend fun getUserRates(
        userId: Long,
        page: Int? = null,
        limit: Int? = null,
        status: String? = null,
        targetType: String? = null,
        censored: Boolean? = true
    ): List<UserRate>

    suspend fun updateUserRate(
        id: Long,
        request: UserRateRequest
    ): Result<UserRateResponse>

    suspend fun createUserRate(createRequest: CreateUserRateRequest): Result<UserRateResponse>
}