package com.example.shikiflow.data.anime

import com.example.graphql.AnimeTracksQuery

data class AnimeTracksResponse(
    val userRates: List<AnimeTracksQuery.UserRate>,
    val hasNextPage: Boolean
)