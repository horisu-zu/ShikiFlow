package com.example.shikiflow.domain.repository

import com.example.graphql.AnimeBrowseQuery
import com.example.graphql.AnimeDetailsQuery
import com.example.graphql.type.DurationString
import com.example.graphql.type.OrderEnum
import com.example.graphql.type.RatingString
import com.example.graphql.type.SeasonString
import com.example.shikiflow.domain.model.anime.MyListString
import com.example.shikiflow.domain.model.anime.SimilarAnime
import com.example.shikiflow.domain.model.common.ExternalLink

interface AnimeRepository {
    suspend fun getAnimeDetails(id: String): AnimeDetailsQuery.Anime?
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
    ): Result<List<AnimeBrowseQuery.Anime>>

    suspend fun getSimilarAnime(id: String): List<SimilarAnime>
    suspend fun getExternalLinks(id: String): List<ExternalLink>
}