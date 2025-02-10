package com.example.shikiflow.domain.repository

import android.util.Log
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.example.graphql.MangaBrowseQuery
import com.example.graphql.MangaDetailsQuery
import com.example.graphql.ShortMangaTracksQuery
import com.example.graphql.type.OrderEnum
import com.example.graphql.type.UserRateOrderInputType
import com.example.graphql.type.UserRateStatusEnum
import com.example.shikiflow.data.anime.MyListString
import com.example.shikiflow.data.anime.ShortMangaTracksResponse
import com.example.shikiflow.data.anime.toGraphQLValue
import com.example.shikiflow.data.manga.MangaResponse
import javax.inject.Inject

class MangaRepository @Inject constructor(
    private val apolloClient: ApolloClient
) {

    suspend fun getMangaDetails(
        id: String
    ): Result<MangaDetailsQuery.Manga?> {
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

    suspend fun browseManga(
        name: String? = null,
        page: Int = 1,
        limit: Int = 45,
        userStatus: MyListString? = null,
        searchInUserList: Boolean = true,
        order: OrderEnum? = null,
        kind: String? = null,
        status: String? = null,
        score: Int? = null,
        genre: String? = null,
        publisher: String? = null,
        franchise: String? = null,
        censored: Boolean? = null
    ): Result<MangaResponse> {
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
                Result.success(
                    MangaResponse(
                        mangaList = data.mangas,
                        hasNextPage = data.mangas.size >= limit
                    )
                )
            } ?: Result.failure(Exception("No data"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

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