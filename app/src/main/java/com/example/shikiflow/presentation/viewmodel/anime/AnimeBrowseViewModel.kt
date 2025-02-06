package com.example.shikiflow.presentation.viewmodel.anime

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.graphql.type.AnimeKindEnum
import com.example.graphql.type.AnimeStatusEnum
import com.example.graphql.type.OrderEnum
import com.example.graphql.type.SeasonString
import com.example.shikiflow.data.anime.BrowseState
import com.example.shikiflow.data.anime.BrowseType
import com.example.shikiflow.data.mapper.BrowseOptions
import com.example.shikiflow.data.mapper.BrowseParams
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
        name: String? = null,
        isLoadingMore: Boolean = false
    ) {
        val options = BrowseParams.animeParams[type] ?: BrowseOptions()
        val stateFlow = animeStateMap[type] ?: return
        val currentState = stateFlow.value

        if (currentState.isLoading || !currentState.hasMorePages) return

        if (!isLoadingMore) {
            stateFlow.update { it.copy(isLoading = true) }
        }

        viewModelScope.launch {
            val result = animeRepository.browseAnime(
                name = name,
                page = currentState.currentPage,
                limit = 45,
                searchInUserList = false,
                status = options.status?.rawValue,
                order = options.order,
                kind = options.kind,
                season = options.season,
                genre = options.genre
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
                        currentPage = currentState.currentPage + 1,
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