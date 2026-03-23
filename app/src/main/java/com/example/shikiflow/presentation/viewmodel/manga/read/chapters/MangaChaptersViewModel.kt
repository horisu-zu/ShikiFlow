package com.example.shikiflow.presentation.viewmodel.manga.read.chapters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shikiflow.domain.model.sort.SortDirection
import com.example.shikiflow.domain.usecase.AggregateMangaUseCase
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
class MangaChaptersViewModel @Inject constructor(
    private val aggregateMangaUseCase: AggregateMangaUseCase
): ViewModel() {

    private val _chaptersUiState = MutableStateFlow(MangaChaptersUiState())
    val chaptersUiState = _chaptersUiState.asStateFlow()

    init {
        _chaptersUiState
            .filter { state ->
                state.mangaDexId != null
            }
            .distinctUntilChanged { old, new ->
                old.mangaDexId == new.mangaDexId && !new.isRefreshing
            }
            .flatMapLatest { state ->
                aggregateMangaUseCase(state.mangaDexId!!)
            }.onEach { result ->
                when(result) {
                    DataResult.Loading -> {
                        _chaptersUiState.update { state ->
                            state.copy(
                                isLoading = true,
                                isRefreshing = false
                            )
                        }
                    }
                    is DataResult.Success -> {
                        _chaptersUiState.update { state ->
                            state.copy(
                                isLoading = false,
                                errorMessage = null,
                                chaptersMap = result.data
                            )
                        }
                    }
                    is DataResult.Error -> {
                        _chaptersUiState.update { state ->
                            state.copy(
                                isLoading = false,
                                errorMessage = result.message
                            )
                        }
                    }
                }
            }.launchIn(viewModelScope)
    }

    fun setId(mangaDexId: String) {
        _chaptersUiState.update { state ->
            state.copy(mangaDexId = mangaDexId)
        }
    }

    fun changeDirection(sortDirection: SortDirection) {
        _chaptersUiState.update { state ->
            state.copy(sortDirection = sortDirection)
        }
    }

    fun onRefresh() {
        _chaptersUiState.update { state ->
            state.copy(isRefreshing = true)
        }
    }
}