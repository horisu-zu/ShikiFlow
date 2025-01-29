package com.example.shikiflow.domain.repository

import android.util.Log
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.example.graphql.AnimeTracksQuery
import com.example.graphql.ShortAnimeTracksQuery
import com.example.graphql.type.UserRateOrderInputType
import com.example.graphql.type.UserRateStatusEnum
import com.example.shikiflow.data.anime.AnimeTracksResponse
import com.example.shikiflow.data.anime.ShortAnimeTracksResponse
import javax.inject.Inject

class AnimeTracksRepository @Inject constructor(
    private val apolloClient: ApolloClient
) {
    suspend fun getAnimeTracks(
        page: Int = 1,
        limit: Int = 50,
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

    suspend fun getShortAnimeTracks(
        page: Int = 1,
        limit: Int = 50,
        userId: String? = null,
        status: UserRateStatusEnum? = null,
        order: UserRateOrderInputType? = null
    ): Result<ShortAnimeTracksResponse> {
        val query = ShortAnimeTracksQuery(
            page = Optional.presentIfNotNull(page),
            limit = Optional.presentIfNotNull(limit),
            userId = Optional.presentIfNotNull(userId),
            status = Optional.presentIfNotNull(status),
            order = Optional.presentIfNotNull(order)
        )

        return try {
            val response = apolloClient.query(query).execute()
            response.data?.let { data ->
                Log.d("ShortAnimeTracks", response.data?.userRates?.size.toString())
                Result.success(
                    ShortAnimeTracksResponse(
                        userRates = data.userRates,
                        hasNextPage = data.userRates.size >= limit
                    )
                )
            } ?: Result.failure(Exception("No data"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}