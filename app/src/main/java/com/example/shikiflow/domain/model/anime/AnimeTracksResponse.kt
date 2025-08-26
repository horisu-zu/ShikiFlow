package com.example.shikiflow.domain.model.anime

import com.example.graphql.AnimeTracksQuery

data class AnimeTracksResponse(
    val userRates: List<AnimeTracksQuery.UserRate>,
    val hasNextPage: Boolean
)