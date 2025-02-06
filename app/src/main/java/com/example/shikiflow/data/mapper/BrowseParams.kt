package com.example.shikiflow.data.mapper

import com.example.graphql.type.AnimeKindEnum
import com.example.graphql.type.AnimeStatusEnum
import com.example.graphql.type.OrderEnum
import com.example.graphql.type.SeasonString
import com.example.shikiflow.data.anime.BrowseType

object BrowseParams {
    val animeParams = mapOf(
        BrowseType.AnimeBrowseType.ONGOING to BrowseOptions(
            status = AnimeStatusEnum.ongoing,
            order = OrderEnum.ranked
        ),
        BrowseType.AnimeBrowseType.TOP to BrowseOptions(
            order = OrderEnum.ranked
        ),
        BrowseType.AnimeBrowseType.ONGOING_CALENDAR to BrowseOptions(
            status = AnimeStatusEnum.ongoing
        )
    )
}

data class BrowseOptions(
    val status: AnimeStatusEnum? = null,
    val order: OrderEnum? = null,
    val kind: AnimeKindEnum? = null,
    val season: SeasonString? = null,
    val genre: String? = null
)