package com.example.shikiflow.data.anime

import com.example.graphql.AnimeBrowseQuery

data class AnimeResponse(
    val animeList: List<AnimeBrowseQuery.Anime>,
    val hasNextPage: Boolean
)