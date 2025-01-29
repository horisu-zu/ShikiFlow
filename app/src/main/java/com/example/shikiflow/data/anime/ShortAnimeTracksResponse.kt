package com.example.shikiflow.data.anime

import com.example.graphql.ShortAnimeTracksQuery

data class ShortAnimeTracksResponse(
    val userRates: List<ShortAnimeTracksQuery.UserRate>,
    val hasNextPage: Boolean
)
