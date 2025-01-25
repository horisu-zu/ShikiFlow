package com.example.shikiflow.data.anime

import com.example.graphql.AnimeTracksQuery

data class AnimeResponse(
    val userRates: List<AnimeTracksQuery.UserRate>,
    val hasNextPage: Boolean
)