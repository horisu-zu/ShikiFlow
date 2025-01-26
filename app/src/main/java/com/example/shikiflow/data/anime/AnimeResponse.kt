package com.example.shikiflow.data.anime

import com.example.graphql.AnimeTracksV2Query

data class AnimeResponse(
    val animeList: List<AnimeTracksV2Query.Anime>,
    val hasNextPage: Boolean
)