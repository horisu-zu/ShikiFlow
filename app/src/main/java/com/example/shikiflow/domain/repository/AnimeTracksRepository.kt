package com.example.shikiflow.domain.repository

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.example.graphql.AnimeTracksQuery
import com.example.graphql.type.UserRateOrderInputType
import com.example.graphql.type.UserRateStatusEnum
import com.example.shikiflow.data.anime.AnimeTracksResponse
import javax.inject.Inject

class AnimeTracksRepository @Inject constructor(
    private val apolloClient: ApolloClient
) {
    suspend fun getAnimeTracks(
        page: Int = 1,
        limit: Int = 20,
        userId: String? = null,
        status: UserRateStatusEnum? = null,
        order: UserRateOrderInputType? = null
    ): Result<AnimeTracksResponse> {
        val query = AnimeTracksQuery(
            page = Optional.presentIfNotNull(page),
            limit = Optional.presentIfNotNull(limit),
            userId = Optional.presentIfNotNull(userId),
            status = Optional.presentIfNotNull(status),
            order = Optional.presentIfNotNull(order)
        )

        return try {
            val response = apolloClient.query(query).execute()
            response.data?.let {
                Result.success(
                    AnimeTracksResponse(
                        userRates = it.userRates,
                        hasNextPage = it.userRates.size >= limit
                    )
                )
            } ?: Result.failure(Exception("No data"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}