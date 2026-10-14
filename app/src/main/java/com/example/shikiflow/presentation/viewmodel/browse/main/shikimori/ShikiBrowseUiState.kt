package com.example.shikiflow.presentation.viewmodel.browse.main.shikimori

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.shikiflow.domain.model.browse.BrowseMedia
import com.example.shikiflow.domain.model.sort.MediaSort
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.presentation.PagedUiState

data class ShikiBrowseUiState(
    val mediaType: MediaType = MediaType.ANIME,
    val mainSections: Map<MediaType, ShikiBrowseMediaUiState> =
        MediaType.entries.associateWith { ShikiBrowseMediaUiState() }
)

data class ShikiBrowseMediaUiState(
    val entries: SnapshotStateList<BrowseMedia> = mutableStateListOf(),
    val sort: MediaSort? = null,
    override val page: Int = 1,
    override val hasNextPage: Boolean = true,
    override val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    override val errorMessage: String? = null
): PagedUiState() {
    override fun setError(value: String?) = copy(errorMessage = value)
    override fun setLoading(value: Boolean) = copy(isLoading = value)
    override fun setPage(value: Int) = copy(page = value)
    override fun setHasNextPage(value: Boolean) = copy(hasNextPage = value)
}