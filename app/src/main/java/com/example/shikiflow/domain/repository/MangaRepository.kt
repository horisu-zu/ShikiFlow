package com.example.shikiflow.domain.repository

import android.util.Log
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.example.graphql.ShortMangaTracksQuery
import com.example.graphql.type.UserRateOrderInputType
import com.example.graphql.type.UserRateStatusEnum
import com.example.shikiflow.data.anime.ShortMangaTracksResponse
import javax.inject.Inject

class MangaRepository @Inject constructor(
    private val apolloClient: ApolloClient
) {

    suspend fun getShortMangaTracks(
        page: Int = 1,
        limit: Int = 50,
        userId: String? = null,
        status: UserRateStatusEnum? = null,
        order: UserRateOrderInputType? = null
    ): Result<ShortMangaTracksResponse> {
        val query = ShortMangaTracksQuery(
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
                    ShortMangaTracksResponse(
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