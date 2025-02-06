package com.example.shikiflow.data.manga

import com.example.graphql.MangaBrowseQuery

data class MangaResponse(
    val mangaList: List<MangaBrowseQuery.Manga>,
    val hasNextPage: Boolean
)
