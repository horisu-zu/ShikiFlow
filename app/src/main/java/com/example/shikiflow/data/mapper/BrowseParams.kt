package com.example.shikiflow.data.mapper

import com.example.graphql.type.AnimeStatusEnum
import com.example.graphql.type.OrderEnum
import com.example.graphql.type.SeasonString
import com.example.shikiflow.data.anime.BrowseType
import com.example.shikiflow.data.anime.MyListString
import com.example.shikiflow.data.tracks.MediaType

object BrowseParams {
    val animeParams = mapOf(
        BrowseType.AnimeBrowseType.ONGOING to BrowseOptions(
            mediaType = MediaType.ANIME,
            status = AnimeStatusEnum.ongoing
        ),
        BrowseType.AnimeBrowseType.ANIME_TOP to BrowseOptions(
            mediaType = MediaType.ANIME
        ),
        BrowseType.AnimeBrowseType.SEARCH to BrowseOptions(
            mediaType = MediaType.ANIME
        )
    )
    val mangaParams = mapOf(
        BrowseType.MangaBrowseType.MANGA_TOP to BrowseOptions(
            mediaType = MediaType.MANGA
        ),
        BrowseType.MangaBrowseType.SEARCH to BrowseOptions(
            mediaType = MediaType.MANGA
        )
    )
}

data class BrowseOptions(
    val mediaType: MediaType,
    val status: Enum<*>? = null,
    val order: OrderEnum? = OrderEnum.ranked,
    val kind: Enum<*>? = null,
    val season: SeasonString? = null,
    val genre: String? = null,
    val userListStatus: List<MyListString> = emptyList()
)