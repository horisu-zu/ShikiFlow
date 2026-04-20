package com.example.shikiflow.presentation.viewmodel.anime.studio

import com.example.shikiflow.domain.model.sort.SortType
import com.example.shikiflow.domain.model.studio.Studio
import com.example.shikiflow.presentation.UiState

data class StudioUiState(
    val studioId: Int? = null,
    val query: String = "",
    val studio: Studio? = null,

    val onUserList: Boolean? = null,
    val sortType: SortType? = null,
    val isRefreshing: Boolean = false,
    override val isLoading: Boolean = true,
    override val errorMessage: String? = null
): UiState() {
    override fun setError(value: String?) = copy(errorMessage = value)
    override fun setLoading(value: Boolean) = copy(isLoading = value)
}