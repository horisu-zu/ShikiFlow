package com.example.shikiflow.presentation.viewmodel.manga

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shikiflow.data.anime.BrowseState
import com.example.shikiflow.data.anime.BrowseType
import com.example.shikiflow.data.mapper.BrowseOptions
import com.example.shikiflow.data.mapper.BrowseParams
import com.example.shikiflow.data.tracks.MediaType
import com.example.shikiflow.domain.repository.MangaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MangaBrowseViewModel @Inject constructor(
    private val mangaRepository: MangaRepository
): ViewModel() {

    private val mangaStateMap = BrowseType.MangaBrowseType.entries.associateWith {
        MutableStateFlow<BrowseState.MangaBrowseState>(BrowseState.MangaBrowseState())
    }

    fun getMangaState(type: BrowseType.MangaBrowseType) = mangaStateMap[type]?.asStateFlow()
        ?: throw IllegalArgumentException("Unknown anime type")

    fun browseManga(
        type: BrowseType = BrowseType.MangaBrowseType.MANGA_TOP,
        options: BrowseOptions = BrowseParams.mangaParams[type] ?: BrowseOptions(mediaType = MediaType.MANGA),
        name: String? = null,
        isLoadingMore: Boolean = false
    ) {
        val stateFlow = mangaStateMap[type] ?: return
        val currentState = stateFlow.value

        if (currentState.isLoading || !currentState.hasMorePages) return

        if (!isLoadingMore) {
            stateFlow.update { it.copy(isLoading = true) }
        }

        viewModelScope.launch {
            val result = mangaRepository.browseManga(
                name = name,
                page = currentState.currentPage,
                limit = 45,
                searchInUserList = false,
                status = options.status?.name,
                order = options.order,
                kind = options.kind?.name,
                genre = options.genre
            )

            result.onSuccess { response ->
                stateFlow.update { currentState ->
                    currentState.copy(
                        items = if (isLoadingMore) {
                            currentState.items + response.mangaList
                        } else {
                            response.mangaList
                        },
                        hasMorePages = response.hasNextPage,
                        currentPage = currentState.currentPage + 1,
                        isLoading = false,
                        error = null
                    )
                }
            }.onFailure { error ->
                Log.d("MangaBrowseViewModel", "Error loading titles: ${error.message}")
                stateFlow.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        hasMorePages = false,
                        error = if (currentState.items.isEmpty()) error.message else null
                    )
                }
            }
        }
    }
}