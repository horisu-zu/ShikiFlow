package com.example.shikiflow.presentation.viewmodel.browse.main.anilist

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shikiflow.domain.model.browse.BrowseType
import com.example.shikiflow.domain.usecase.BrowseUseCase
import com.example.shikiflow.utils.DataResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class AnilistBrowseViewModel @Inject constructor(
    private val browseUseCase: BrowseUseCase
): ViewModel() {

    private val _uiState = MutableStateFlow(AnilistBrowseUiState())
    val uiState = _uiState.asStateFlow()

    fun onRetry(browseType: BrowseType) {
        _uiState.update { state ->
            state.updateSection(browseType) { section ->
                section.copy(isRefreshing = true)
            }
        }
    }

    fun onRefresh() {
        _uiState.update { state ->
            state.copy(
                sections = state.sections.mapValues { (_, section) ->
                    section.copy(isRefreshing = true)
                }
            )
        }
    }

    fun fetchBrowseData(browseType: BrowseType) {
        _uiState
            .filter { state ->
                val section = state.sections[browseType]

                (section?.browseMedia.isNullOrEmpty() && section?.errorMessage == null) ||
                section.isRefreshing
            }
            .distinctUntilChanged { old, new ->
                new.sections[browseType]?.isRefreshing == false
            }
            .flatMapLatest { browseUseCase(browseType) }
            .onEach { result ->
                _uiState.update { state ->
                    when(result) {
                        is DataResult.Loading -> {
                            state.updateSection(browseType) { section ->
                                section.copy(
                                    isLoading = true,
                                    errorMessage = null,
                                    isRefreshing = false
                                )
                            }
                        }
                        is DataResult.Success -> {
                            state.updateSection(browseType) { section ->
                                section.copy(
                                    browseMedia = result.data,
                                    isLoading = false
                                )
                            }
                        }
                        is DataResult.Error -> {
                            Log.d("BrowseViewModel", "Error: ${result.message}")
                            state.updateSection(browseType) { section ->
                                section.copy(
                                    errorMessage = result.message,
                                    isLoading = false
                                )
                            }
                        }
                    }
                }
            }.launchIn(viewModelScope)
    }

    private fun AnilistBrowseUiState.updateSection(
        type: BrowseType,
        transform: (AnilistBrowseSectionUiState) -> AnilistBrowseSectionUiState
    ): AnilistBrowseUiState {
        return copy(
            sections = sections + (type to transform(sections[type] ?: AnilistBrowseSectionUiState()))
        )
    }
}