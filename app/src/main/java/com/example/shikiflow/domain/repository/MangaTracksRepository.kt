package com.example.shikiflow.domain.repository

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.example.graphql.MangaTracksQuery
import com.example.graphql.type.UserRateOrderInputType
import com.example.graphql.type.UserRateStatusEnum
import javax.inject.Inject

class MangaTracksRepository @Inject constructor(
    private val apolloClient: ApolloClient
) {
    suspend fun getMangaTracks(
        page: Int = 1,
        limit: Int = 50,
        userId: String? = null,
        status: UserRateStatusEnum? = null,
        order: UserRateOrderInputType? = null
    ): Result<List<MangaTracksQuery.UserRate>> {
        val query = MangaTracksQuery(
            page = Optional.presentIfNotNull(page),
            limit = Optional.presentIfNotNull(limit),
            userId = Optional.presentIfNotNull(userId),
            status = Optional.presentIfNotNull(status),
            order = Optional.presentIfNotNull(order)
        )

        return try {
            val response = apolloClient.query(query).execute()
            response.data?.let {
                Result.success(it.userRates)
            } ?: Result.failure(Exception("No data"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}