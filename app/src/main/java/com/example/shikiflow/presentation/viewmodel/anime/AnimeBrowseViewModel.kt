package com.example.shikiflow.presentation.viewmodel.anime

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.graphql.type.AnimeKindEnum
import com.example.graphql.type.AnimeStatusEnum
import com.example.graphql.type.OrderEnum
import com.example.graphql.type.SeasonString
import com.example.shikiflow.data.anime.AnimeBrowseState
import com.example.shikiflow.data.anime.AnimeBrowseType
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

    private val stateMap = AnimeBrowseType.entries.associateWith {
        MutableStateFlow(AnimeBrowseState())
    }

    fun getAnimeState(type: AnimeBrowseType) = stateMap[type]?.asStateFlow()
        ?: throw IllegalArgumentException("Unknown list type")

    fun browseAnime(
        type: AnimeBrowseType = AnimeBrowseType.ONGOING,
        name: String? = null,
        status: AnimeStatusEnum? = null,
        order: OrderEnum? = OrderEnum.ranked,
        kind: AnimeKindEnum? = null,
        season: SeasonString? = null,
        genre: String? = null,
        isLoadingMore: Boolean = false
    ) {
        val stateFlow = stateMap[type] ?: return
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
                status = status?.rawValue,
                order = order,
                kind = kind,
                season = season,
                genre = genre
            )

            result.onSuccess { response ->
                stateFlow.update {
                    it.copy(
                        items = if (isLoadingMore) it.items + response.animeList else response.animeList,
                        hasMorePages = response.hasNextPage,
                        currentPage = it.currentPage + 1,
                        isLoading = false,
                        error = null
                    )
                }
            }.onFailure { error ->
                Log.d("AnimeBrowseViewModel", "Error loading titles: ${error.message}")
                stateFlow.update {
                    it.copy(
                        isLoading = false,
                        hasMorePages = false,
                        error = if (it.items.isEmpty()) error.message else null
                    )
                }
            }
        }
    }

    fun resetState(type: AnimeBrowseType) {
        stateMap[type]?.value = AnimeBrowseState()
    }
}