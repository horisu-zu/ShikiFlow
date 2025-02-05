package com.example.shikiflow.presentation.viewmodel.anime

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.graphql.AnimeBrowseQuery
import com.example.graphql.type.OrderEnum
import com.example.shikiflow.data.anime.MyListString
import com.example.shikiflow.domain.repository.AnimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.toInstant
import javax.inject.Inject

@HiltViewModel
class AnimeTracksSearchViewModel @Inject constructor(
    private val animeRepository: AnimeRepository
): ViewModel() {
    private val _searchResults = MutableStateFlow<Map<MyListString?, List<AnimeBrowseQuery.Anime>>>(
        MyListString.entries.associateWith { emptyList() }
    )
    val searchResults = _searchResults.asStateFlow()

    private val _isSearching = MutableStateFlow<Map<MyListString?, Boolean>>(
        MyListString.entries.associateWith { false }
    )
    val isSearching = _isSearching.asStateFlow()

    private val _searchHasMorePages = MutableStateFlow<Map<MyListString?, Boolean>>(
        MyListString.entries.associateWith { true }
    )
    val searchHasMorePages = _searchHasMorePages.asStateFlow()

    private val currentSearchPage = MutableStateFlow<Map<MyListString?, Int>>(
        MyListString.entries.associateWith { 1 }
    )
    private var currentSearchQuery = ""

    fun searchAnimeTracks(
        name: String,
        status: MyListString? = null,
        isRefresh: Boolean = false
    ) {
        if (_isSearching.value[status] == true && !isRefresh) return

        viewModelScope.launch {
            _isSearching.update {
                it.toMutableMap().apply { this[status] = true }
            }

            if (isRefresh || name != currentSearchQuery) {
                currentSearchQuery = name
                _searchResults.update {
                    it.toMutableMap().apply { this[status] = emptyList() }
                }
                _searchHasMorePages.update {
                    it.toMutableMap().apply { this[status] = true }
                }
                currentSearchPage.update {
                    it.toMutableMap().apply { this[status] = 1 }
                }
            }

            val currentPage = currentSearchPage.value[status] ?: 1

            val result = animeRepository.browseAnime(
                name = name,
                page = currentPage,
                userStatus = status,
                limit = 20,
                order = OrderEnum.popularity
            )

            result.onSuccess { response ->
                _searchResults.update { currentResults ->
                    currentResults.toMutableMap().apply {
                        val newResults = response.animeList
                        val existingResults = currentResults[status] ?: emptyList()

                        this[status] = if (isRefresh) {
                            newResults
                        } else {
                            (existingResults + newResults)
                                .distinctBy { it.id }
                                .sortedByDescending {
                                    (it.updatedAt as? String)?.toInstant() ?: Instant.DISTANT_PAST
                                }
                        }
                    }
                }

                _searchHasMorePages.update {
                    it.toMutableMap().apply { this[status] = response.hasNextPage }
                }

                currentSearchPage.update {
                    it.toMutableMap().apply {
                        this[status] = currentPage + 1
                    }
                }
            }.onFailure {
                _searchHasMorePages.update {
                    it.toMutableMap().apply { this[status] = false }
                }
            }

            _isSearching.update {
                it.toMutableMap().apply { this[status] = false }
            }
        }
    }

    fun clearSearchResults() {
        _searchResults.update {
            it.toMutableMap().apply {
                keys.forEach { key -> this[key] = emptyList() }
            }
        }
        currentSearchPage.update {
            it.toMutableMap().apply {
                keys.forEach { key -> this[key] = 1 }
            }
        }
        _searchHasMorePages.update {
            it.toMutableMap().apply {
                keys.forEach { key -> this[key] = true }
            }
        }
        currentSearchQuery = ""
    }
}