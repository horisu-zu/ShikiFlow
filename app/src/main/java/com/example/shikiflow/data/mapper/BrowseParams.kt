package com.example.shikiflow.data.mapper

import com.example.graphql.type.AnimeStatusEnum
import com.example.graphql.type.OrderEnum
import com.example.graphql.type.SeasonString
import com.example.shikiflow.data.anime.BrowseType
import com.example.shikiflow.data.anime.MyListString

object BrowseParams {
    val animeParams = mapOf(
        BrowseType.AnimeBrowseType.ONGOING to BrowseOptions(
            status = AnimeStatusEnum.ongoing
        ),
        BrowseType.AnimeBrowseType.ANIME_TOP to BrowseOptions(),
        BrowseType.AnimeBrowseType.SEARCH to BrowseOptions()
    )
}

data class BrowseOptions(
    val status: Enum<*>? = null,
    val order: OrderEnum? = OrderEnum.ranked,
    val kind: Enum<*>? = null,
    val season: SeasonString? = null,
    val genre: String? = null,
    val userListStatus: List<MyListString> = emptyList()
)