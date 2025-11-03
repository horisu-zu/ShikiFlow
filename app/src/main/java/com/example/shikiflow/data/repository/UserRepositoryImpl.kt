package com.example.shikiflow.data.repository

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.example.graphql.CurrentUserQuery
import com.example.graphql.UsersQuery
import com.example.shikiflow.data.remote.UserApi
import com.example.shikiflow.domain.model.tracks.CreateUserRateRequest
import com.example.shikiflow.domain.model.tracks.TargetType
import com.example.shikiflow.domain.model.tracks.UserRate
import com.example.shikiflow.domain.model.tracks.UserRateRequest
import com.example.shikiflow.domain.model.tracks.UserRateResponse
import com.example.shikiflow.domain.model.user.User
import com.example.shikiflow.domain.model.user.User.Companion.toDomain
import com.example.shikiflow.domain.model.user.UserFavoritesResponse
import com.example.shikiflow.domain.model.user.UserHistoryResponse
import com.example.shikiflow.domain.model.userrate.ShortUserRate
import com.example.shikiflow.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val apolloClient: ApolloClient,
    private val userApi: UserApi
): UserRepository {

    override suspend fun fetchCurrentUser(): User? {
        val response = apolloClient.query(CurrentUserQuery()).execute()

        return response.data?.currentUser?.toDomain()
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
    ): List<ShortUserRate.ShortAnimeRate> = userApi.getUserAnimeRates(
        userId = userId,
        page = page,
        limit = limit,
        status = status,
        censored = censored
    )

    override suspend fun getUserMangaRates(
        userId: Long,
        page: Int?,
        limit: Int?,
        status: String?,
        censored: Boolean?
    ): List<ShortUserRate.ShortMangaRate> = userApi.getUserMangaRates(
        userId = userId,
        page = page,
        limit = limit,
        status = status,
        censored = censored
    )

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

    override suspend fun getUsersByNickname(
        page: Int,
        limit: Int,
        nickname: String
    ): Result<List<User>> {
        val query = UsersQuery(
            page = Optional.present(page),
            limit = Optional.present(limit),
            search = Optional.present(nickname)
        )

        return try {
            val response = apolloClient.query(query).execute()

            response.data?.let { users ->
                Result.success(users.users.map { it.toDomain() })
            } ?: Result.failure(Exception("No data"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateUserRate(
        id: Long,
        request: UserRateRequest
    ): UserRateResponse = userApi.updateUserRate(
        id = id,
        request = request
    )

    override suspend fun createUserRate(createRequest: CreateUserRateRequest): UserRateResponse =
        userApi.createUserRate(createRequest)

    override suspend fun getUserFavorites(userId: Long): UserFavoritesResponse = userApi.getUserFavorites(userId)
}