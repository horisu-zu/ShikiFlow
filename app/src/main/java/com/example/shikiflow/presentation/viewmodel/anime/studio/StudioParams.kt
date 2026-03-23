package com.example.shikiflow.presentation.viewmodel.anime.studio

import com.example.shikiflow.domain.model.sort.SortType

data class StudioParams(
    val studioId: Int? = null,
    val query: String = "",
    val onUserList: Boolean? = null,
    val sortType: SortType? = null
)