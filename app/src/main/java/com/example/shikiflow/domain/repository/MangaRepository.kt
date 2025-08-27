package com.example.shikiflow.domain.repository

import com.example.graphql.MangaBrowseQuery
import com.example.graphql.MangaDetailsQuery
import com.example.graphql.type.OrderEnum
import com.example.graphql.type.UserRateOrderInputType
import com.example.graphql.type.UserRateStatusEnum
import com.example.shikiflow.domain.model.anime.MyListString
import com.example.shikiflow.domain.model.anime.ShikiManga
import com.example.shikiflow.domain.model.anime.ShortMangaTracksResponse
import com.example.shikiflow.domain.model.common.ExternalLink

interface MangaRepository {
    suspend fun getMangaDetails(id: String): Result<MangaDetailsQuery.Manga?>
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
    ): Result<List<MangaBrowseQuery.Manga>>

    suspend fun getShortMangaTracks(
        page: Int = 1,
        limit: Int = 50,
        userId: String? = null,
        status: UserRateStatusEnum? = null,
        order: UserRateOrderInputType? = null
    ): Result<ShortMangaTracksResponse>

    suspend fun getSimilarManga(id: String): List<ShikiManga>

    suspend fun getExternalLinks(id: String): List<ExternalLink>
}