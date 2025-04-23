package com.example.shikiflow.domain.repository

import android.util.Log
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.example.graphql.AnimeBrowseQuery
import com.example.graphql.AnimeDetailsQuery
import com.example.graphql.type.DurationString
import com.example.graphql.type.OrderEnum
import com.example.graphql.type.RatingString
import com.example.graphql.type.SeasonString
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

    suspend fun browseAnime(
        name: String? = null,
        page: Int = 1,
        limit: Int = 45,
        userStatus: List<MyListString?> = emptyList(),
        searchInUserList: Boolean = true,
        order: OrderEnum? = null,
        kind: String? = null,
        status: String? = null,
        season: SeasonString? = null,
        score: Int? = null,
        duration: DurationString? = null,
        rating: RatingString? = null,
        genre: String? = null,
        studio: String? = null,
        franchise: String? = null,
        censored: Boolean? = null,
    ): Result<List<AnimeBrowseQuery.Anime>> {
        val query = AnimeBrowseQuery(
            page = Optional.presentIfNotNull(page),
            limit = Optional.presentIfNotNull(limit),
            search = Optional.presentIfNotNull(name),
            mylist = when {
                !searchInUserList -> Optional.Absent
                userStatus.any { it == null } -> Optional.present(MyListString.entries.joinToString(",") { it.toGraphQLValue() })
                else -> Optional.present(userStatus.joinToString(",") { it?.toGraphQLValue() ?: "" })
            },
            order = Optional.presentIfNotNull(order),
            censored = Optional.presentIfNotNull(censored ?: true),
            kind = Optional.presentIfNotNull(kind),
            status = Optional.presentIfNotNull(status),
            season = Optional.presentIfNotNull(season),
            score = Optional.presentIfNotNull(score),
            duration = Optional.presentIfNotNull(duration),
            rating = Optional.presentIfNotNull(rating),
            genre = Optional.presentIfNotNull(genre),
            studio = Optional.presentIfNotNull(studio),
            franchise = Optional.presentIfNotNull(franchise)
        )
        Log.d("AnimeRepository", "Query: $query")

        return try {
            val response = apolloClient.query(query).execute()
            response.data?.let { data ->
                Result.success(data.animes)
            } ?: Result.failure(Exception("No data"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}