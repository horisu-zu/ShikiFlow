package com.example.shikiflow.data.anime

import com.example.graphql.AnimeBrowseQuery

data class AnimeBrowseState(
    val items: List<AnimeBrowseQuery.Anime> = emptyList(),
    val isLoading: Boolean = false,
    val hasMorePages: Boolean = true,
    val error: String? = null,
    val currentPage: Int = 1
)

enum class AnimeBrowseType {
    ONGOING,
    SEARCH,
    TOP
}