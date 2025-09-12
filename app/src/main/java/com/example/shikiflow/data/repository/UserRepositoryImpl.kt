package com.example.shikiflow.data.repository

import com.apollographql.apollo.ApolloClient
import com.example.graphql.CurrentUserQuery
import com.example.shikiflow.domain.model.manga.ShortMangaRate
import com.example.shikiflow.data.remote.UserApi
import com.example.shikiflow.domain.model.anime.ShortAnimeRate
import com.example.shikiflow.domain.model.tracks.CreateUserRateRequest
import com.example.shikiflow.domain.model.tracks.TargetType
import com.example.shikiflow.domain.model.tracks.UserRate
import com.example.shikiflow.domain.model.tracks.UserRateRequest
import com.example.shikiflow.domain.model.tracks.UserRateResponse
import com.example.shikiflow.domain.model.user.UserHistoryResponse
import com.example.shikiflow.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val apolloClient: ApolloClient,
    private val userApi: UserApi
): UserRepository {

    override suspend fun fetchCurrentUser(): Result<CurrentUserQuery.Data> {
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

    override suspend fun getUserHistory(
        userId: Long,
        page: Int?,
        limit: Int?,
        targetId: Long?,
        targetType: TargetType?
    ): Result<List<UserHistoryResponse>> = runCatching {
        userApi.getUserHistory(
            userId = userId,
            page = page,
            limit = limit,
            targetId = targetId,
            targetType = targetType?.name
        )
    }

    override suspend fun getUserAnimeRates(
        userId: Long,
        page: Int?,
        limit: Int?,
        status: String?,
        censored: Boolean?
    ): Result<List<ShortAnimeRate>> = runCatching {
        userApi.getUserAnimeRates(
            userId = userId,
            page = page,
            limit = limit,
            status = status,
            censored = censored
        )
    }

    override suspend fun getUserMangaRates(
        userId: Long,
        page: Int?,
        limit: Int?,
        status: String?,
        censored: Boolean?
    ): Result<List<ShortMangaRate>> = runCatching {
        userApi.getUserMangaRates(
            userId = userId,
            page = page,
            limit = limit,
            status = status,
            censored = censored
        )
    }

    override suspend fun getUserRates(
        userId: Long,
        page: Int?,
        limit: Int?,
        status: String?,
        targetType: String?,
        censored: Boolean?
    ): List<UserRate> = userApi.getUserRates(
        userId = userId,
        page = page,
        limit = limit,
        status = status,
        targetType = targetType,
        censored = censored
    )

    override suspend fun updateUserRate(
        id: Long,
        request: UserRateRequest
    ): Result<UserRateResponse> = runCatching {
        userApi.updateUserRate(
            id = id,
            request = request
        )
    }

    override suspend fun createUserRate(createRequest: CreateUserRateRequest): Result<UserRateResponse> = runCatching {
        userApi.createUserRate(createRequest)
    }
}