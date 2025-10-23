package com.example.shikiflow.domain.repository

import com.example.shikiflow.domain.model.tracks.CreateUserRateRequest
import com.example.shikiflow.domain.model.tracks.TargetType
import com.example.shikiflow.domain.model.user.UserHistoryResponse
import com.example.shikiflow.domain.model.tracks.UserRate
import com.example.shikiflow.domain.model.tracks.UserRateRequest
import com.example.shikiflow.domain.model.tracks.UserRateResponse
import com.example.shikiflow.domain.model.user.User
import com.example.shikiflow.domain.model.userrate.ShortUserRate

interface UserRepository {
    suspend fun fetchCurrentUser(): User?
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
        limit: Int? = 2000,
        status: String? = null,
        censored: Boolean? = true
    ): List<ShortUserRate.ShortAnimeRate>

    suspend fun getUserMangaRates(
        userId: Long,
        page: Int? = null,
        limit: Int? = 2000,
        status: String? = null,
        censored: Boolean? = true
    ): List<ShortUserRate.ShortMangaRate>

    suspend fun getUserRates(
        userId: Long,
        page: Int? = null,
        limit: Int? = null,
        status: String? = null,
        targetType: String? = null,
        censored: Boolean? = true
    ): List<UserRate>

    suspend fun getUsersByNickname(
        page: Int,
        limit: Int,
        nickname: String
    ): Result<List<User>>

    suspend fun updateUserRate(
        id: Long,
        request: UserRateRequest
    ): UserRateResponse

    suspend fun createUserRate(createRequest: CreateUserRateRequest): UserRateResponse
}