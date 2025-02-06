package com.example.shikiflow.data.anime

import com.example.graphql.AnimeBrowseQuery
import com.example.graphql.MangaBrowseQuery

sealed interface BrowseState {
    val isLoading: Boolean
    val hasMorePages: Boolean
    val currentPage: Int
    val error: String?

    data class AnimeBrowseState(
        val items: List<AnimeBrowseQuery.Anime> = emptyList(),
        override val isLoading: Boolean = false,
        override val hasMorePages: Boolean = true,
        override val currentPage: Int = 1,
        override val error: String? = null
    ) : BrowseState

    data class MangaBrowseState(
        val items: List<MangaBrowseQuery.Manga> = emptyList(),
        override val isLoading: Boolean = false,
        override val hasMorePages: Boolean = true,
        override val currentPage: Int = 1,
        override val error: String? = null
    ) : BrowseState
}

sealed interface BrowseType {
    enum class AnimeBrowseType: BrowseType { ONGOING, SEARCH, TOP, ONGOING_CALENDAR }
    enum class MangaBrowseType: BrowseType { SEARCH, TOP }
}
