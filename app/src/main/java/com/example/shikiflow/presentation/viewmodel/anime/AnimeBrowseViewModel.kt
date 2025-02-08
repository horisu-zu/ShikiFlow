package com.example.shikiflow.presentation.viewmodel.anime

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shikiflow.data.anime.BrowseState
import com.example.shikiflow.data.anime.BrowseType
import com.example.shikiflow.data.mapper.BrowseOptions
import com.example.shikiflow.data.mapper.BrowseParams
import com.example.shikiflow.data.tracks.MediaType
import com.example.shikiflow.domain.repository.AnimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnimeBrowseViewModel @Inject constructor(
    private val animeRepository: AnimeRepository
) : ViewModel() {

    private val animeStateMap = BrowseType.AnimeBrowseType.entries.associateWith {
        MutableStateFlow<BrowseState.AnimeBrowseState>(BrowseState.AnimeBrowseState())
    }

    fun getAnimeState(type: BrowseType.AnimeBrowseType) = animeStateMap[type]?.asStateFlow()
        ?: throw IllegalArgumentException("Unknown anime type")

    fun browseAnime(
        type: BrowseType = BrowseType.AnimeBrowseType.ONGOING,
        options: BrowseOptions = BrowseParams.animeParams[type] ?: BrowseOptions(mediaType = MediaType.ANIME),
        name: String? = null,
        isLoadingMore: Boolean = false
    ) {
        val stateFlow = animeStateMap[type] ?: return
        val currentState = stateFlow.value

        if (currentState.isLoading) return

        if (!isLoadingMore) {
            stateFlow.update { it.copy(isLoading = true, currentPage = 1, items = emptyList()) }
        }

        viewModelScope.launch {
            val page = if (isLoadingMore) currentState.currentPage + 1 else 1

            val result = animeRepository.browseAnime(
                name = name,
                page = page,
                limit = 45,
                searchInUserList = false,
                status = options.status?.name,
                order = options.order,
                kind = options.kind?.name,
                season = options.season,
                genre = options.genre,
                userStatus = options.userListStatus
            )

            result.onSuccess { response ->
                stateFlow.update { currentState ->
                    currentState.copy(
                        items = if (isLoadingMore) {
                            currentState.items + response.animeList
                        } else {
                            response.animeList
                        },
                        hasMorePages = response.hasNextPage,
                        currentPage = page,
                        isLoading = false,
                        error = null
                    )
                }
            }.onFailure { error ->
                Log.d("AnimeBrowseViewModel", "Error loading titles: ${error.message}")
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

    fun resetState(type: BrowseType.AnimeBrowseType) {
        animeStateMap[type]?.value = BrowseState.AnimeBrowseState()
    }
}