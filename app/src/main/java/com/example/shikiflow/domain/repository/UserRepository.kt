package com.example.shikiflow.domain.repository

import com.apollographql.apollo.ApolloClient
import com.example.graphql.CurrentUserQuery
import com.example.shikiflow.data.anime.ShortAnimeRate
import com.example.shikiflow.data.manga.ShortMangaRate
import com.example.shikiflow.data.tracks.CreateUserRateRequest
import com.example.shikiflow.data.tracks.TargetType
import com.example.shikiflow.data.user.UserHistoryResponse
import com.example.shikiflow.data.tracks.UserRate
import com.example.shikiflow.data.tracks.UserRateRequest
import com.example.shikiflow.data.tracks.UserRateResponse
import com.example.shikiflow.di.api.UserApi
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val apolloClient: ApolloClient,
    private val userApi: UserApi
) {

    suspend fun fetchCurrentUser(): Result<CurrentUserQuery.Data> {
        return try {
            val response = apolloClient.query(CurrentUserQuery()).execute()

            if (response.hasErrors()) {
                val errorMessage = response.errors?.joinToString { it.message }
                    ?: "Unknown error occurred"
                Result.failure(Exception(errorMessage))
            } else {
                response.data?.let { data ->
                    Result.success(data)
                } ?: Result.failure(Exception("No data received"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserHistory(
        userId: Long,
        page: Int? = null,
        limit: Int? = null,
        targetId: Long? = null,
        targetType: TargetType? = null
    ): Result<List<UserHistoryResponse>> = runCatching {
        userApi.getUserHistory(
            userId = userId,
            page = page,
            limit = limit,
            targetId = targetId,
            targetType = targetType?.name
        )
    }

    suspend fun getUserAnimeRates(
        userId: Long,
        page: Int? = null,
        limit: Int? = null,
        status: String? = null,
        censored: Boolean? = true
    ): Result<List<ShortAnimeRate>> = runCatching {
        userApi.getUserAnimeRates(
            userId = userId,
            page = page,
            limit = limit,
            status = status,
            censored = censored
        )
    }

    suspend fun getUserMangaRates(
        userId: Long,
        page: Int? = null,
        limit: Int? = null,
        status: String? = null,
        censored: Boolean? = true
    ): Result<List<ShortMangaRate>> = runCatching {
        userApi.getUserMangaRates(
            userId = userId,
            page = page,
            limit = limit,
            status = status,
            censored = censored
        )
    }

    suspend fun getUserRates(
        userId: Long,
        page: Int? = null,
        limit: Int? = null,
        status: String? = null,
        targetType: String? = null,
        censored: Boolean? = true
    ): Result<List<UserRate>> = runCatching {
        userApi.getUserRates(
            userId = userId,
            page = page,
            limit = limit,
            status = status,
            targetType = targetType,
            censored = censored
        )
    }

    suspend fun updateUserRate(
        id: Long,
        request: UserRateRequest
    ): Result<UserRateResponse> = runCatching {
        userApi.updateUserRate(
            id = id,
            request = request
        )
    }

    suspend fun createUserRate(createRequest: CreateUserRateRequest): Result<UserRateResponse> = runCatching {
        userApi.createUserRate(createRequest)
    }
}