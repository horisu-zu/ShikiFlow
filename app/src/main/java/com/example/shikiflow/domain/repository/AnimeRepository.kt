package com.example.shikiflow.domain.repository

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.example.graphql.AnimeDetailsQuery
import com.example.graphql.AnimeTracksV2Query
import com.example.graphql.type.OrderEnum
import com.example.shikiflow.data.anime.AnimeResponse
import com.example.shikiflow.data.anime.MyListString
import com.example.shikiflow.data.anime.toGraphQLValue
import javax.inject.Inject

class AnimeRepository @Inject constructor(
    private val apolloClient: ApolloClient
) {

    suspend fun getAnimeDetails(
        id: String
    ): Result<AnimeDetailsQuery.Anime> {
        val query = AnimeDetailsQuery(
            ids = Optional.presentIfNotNull(id)
        )

        return try {
            val response = apolloClient.query(query).execute()
            response.data?.let {
                Result.success(it.animes.first())
            } ?: Result.failure(Exception("No data"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchAnimeTracks(
        name: String,
        page: Int = 1,
        limit: Int = 20,
        userStatus: MyListString? = null,
        order: OrderEnum? = null
    ): Result<AnimeResponse> {
        val query = AnimeTracksV2Query(
            page = Optional.presentIfNotNull(page),
            limit = Optional.presentIfNotNull(limit),
            search = Optional.presentIfNotNull(name),
            mylist = userStatus?.let { Optional.present(it.toGraphQLValue()) }
                ?: Optional.present(MyListString.entries.joinToString(",") { it.toGraphQLValue() }),
            order = Optional.presentIfNotNull(order),
            censored = Optional.presentIfNotNull(true)
        )

        return try {
            val response = apolloClient.query(query).execute()
            response.data?.let {
                Result.success(
                    AnimeResponse(
                        animeList = it.animes,
                        hasNextPage = it.animes.size >= limit
                    )
                )
            } ?: Result.failure(Exception("No data"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}