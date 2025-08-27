package com.example.shikiflow.data.repository

import android.util.Log
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.example.graphql.AnimeBrowseQuery
import com.example.graphql.AnimeDetailsQuery
import com.example.graphql.type.DurationString
import com.example.graphql.type.OrderEnum
import com.example.graphql.type.RatingString
import com.example.graphql.type.SeasonString
import com.example.shikiflow.data.remote.AnimeApi
import com.example.shikiflow.domain.model.anime.MyListString
import com.example.shikiflow.domain.model.anime.SimilarAnime
import com.example.shikiflow.domain.model.anime.toGraphQLValue
import com.example.shikiflow.domain.model.common.ExternalLink
import com.example.shikiflow.domain.repository.AnimeRepository
import javax.inject.Inject

class AnimeRepositoryImpl @Inject constructor(
    private val apolloClient: ApolloClient,
    private val animeApi: AnimeApi
): AnimeRepository {

    override suspend fun getAnimeDetails(id: String): Result<AnimeDetailsQuery.Anime> {
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

    override suspend fun browseAnime(
        name: String?,
        page: Int,
        limit: Int,
        userStatus: List<MyListString?>,
        searchInUserList: Boolean,
        order: OrderEnum?,
        kind: String?,
        status: String?,
        season: SeasonString?,
        score: Int?,
        duration: DurationString?,
        rating: RatingString?,
        genre: String?,
        studio: String?,
        franchise: String?,
        censored: Boolean?,
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

    override suspend fun getSimilarAnime(id: String): List<SimilarAnime> {
        return try {
            animeApi.getSimilarAnime(id)
        } catch (e: Exception) {
            Log.e("AnimeRepository", "Exception fetching similar anime", e)
            emptyList()
        }
    }

    override suspend fun getExternalLinks(id: String): List<ExternalLink> = animeApi.getExternalLinks(id)
}