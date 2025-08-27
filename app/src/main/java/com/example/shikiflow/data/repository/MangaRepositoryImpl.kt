package com.example.shikiflow.data.repository

import android.util.Log
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.example.graphql.MangaBrowseQuery
import com.example.graphql.MangaDetailsQuery
import com.example.graphql.ShortMangaTracksQuery
import com.example.graphql.type.OrderEnum
import com.example.graphql.type.UserRateOrderInputType
import com.example.graphql.type.UserRateStatusEnum
import com.example.shikiflow.data.remote.MangaApi
import com.example.shikiflow.domain.model.anime.MyListString
import com.example.shikiflow.domain.model.anime.ShikiManga
import com.example.shikiflow.domain.model.anime.ShortMangaTracksResponse
import com.example.shikiflow.domain.model.anime.toGraphQLValue
import com.example.shikiflow.domain.model.common.ExternalLink
import com.example.shikiflow.domain.repository.MangaRepository
import javax.inject.Inject

class MangaRepositoryImpl @Inject constructor(
    private val apolloClient: ApolloClient,
    private val mangaApi: MangaApi
): MangaRepository {

    override suspend fun getMangaDetails(id: String): Result<MangaDetailsQuery.Manga?> {
        val query = MangaDetailsQuery(
            ids = Optional.presentIfNotNull(id)
        )

        return try {
            val response = apolloClient.query(query).execute()
            response.data?.let {
                Result.success(it.mangas.first())
            } ?: Result.failure(Exception("No data"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun browseManga(
        name: String?,
        page: Int,
        limit: Int,
        userStatus: MyListString?,
        searchInUserList: Boolean,
        order: OrderEnum?,
        kind: String?,
        status: String?,
        score: Int?,
        genre: String?,
        publisher: String?,
        franchise: String?,
        censored: Boolean?
    ): Result<List<MangaBrowseQuery.Manga>> {
        val query = MangaBrowseQuery(
            page = Optional.presentIfNotNull(page),
            limit = Optional.presentIfNotNull(limit),
            search = Optional.presentIfNotNull(name),
            mylist = when {
                !searchInUserList -> Optional.Absent
                userStatus != null -> Optional.present(userStatus.toGraphQLValue())
                else -> Optional.present(MyListString.entries.joinToString(",") { it.toGraphQLValue() })
            },
            order = Optional.presentIfNotNull(order),
            censored = Optional.presentIfNotNull(censored ?: true),
            kind = Optional.presentIfNotNull(kind),
            status = Optional.presentIfNotNull(status),
            score = Optional.presentIfNotNull(score),
            genre = Optional.presentIfNotNull(genre),
            publisher = Optional.presentIfNotNull(publisher),
            franchise = Optional.presentIfNotNull(franchise)
        )

        return try {
            val response = apolloClient.query(query).execute()

            response.data?.let { data ->
                Result.success(data.mangas)
            } ?: Result.failure(Exception("No data"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getShortMangaTracks(
        page: Int,
        limit: Int,
        userId: String?,
        status: UserRateStatusEnum?,
        order: UserRateOrderInputType?
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

    override suspend fun getSimilarManga(id: String): List<ShikiManga> {
        return try {
            mangaApi.getSimilarMangas(id)
        } catch (e: Exception) {
            Log.e("MangaRepository", "Exception fetching similar manga", e)
            emptyList()
        }
    }

    override suspend fun getExternalLinks(id: String): List<ExternalLink> = mangaApi.getExternalLinks(id)
}