package com.example.shikiflow.data.anime

import com.example.graphql.ShortMangaTracksQuery

data class ShortMangaTracksResponse(
    val userRates: List<ShortMangaTracksQuery.UserRate?>,
    val hasNextPage: Boolean
)