package com.example.shikiflow.presentation.viewmodel.manga.read.selection

import androidx.lifecycle.viewModelScope
import com.example.shikiflow.domain.model.mangadex.manga.MangaData
import com.example.shikiflow.domain.usecase.GetMangaDexUseCase
import com.example.shikiflow.presentation.UiState
import com.example.shikiflow.presentation.UiStateViewModel
import com.example.shikiflow.utils.DataResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MangaSelectionViewModel @Inject constructor(
    private val getMangaDexUseCase: GetMangaDexUseCase
): UiStateViewModel<MangaSelectionUiState>() {

    override val initialState: MangaSelectionUiState = MangaSelectionUiState()

    fun setMangaDexIds(mangaDexIds: List<String>) {
        mutableUiState.update { state ->
            state.copy(
                mangaDexIds = mangaDexIds
            )
        }
    }

    fun onRefresh() {
        mutableUiState.update { state ->
            state.copy(
                isRefreshing = true
            )
        }
    }

    init {
        mutableUiState
            .filter { state ->
                state.mangaDexIds.isNotEmpty()
            }
            .distinctUntilChanged { old, new ->
                old.mangaDexIds == new.mangaDexIds && !new.isRefreshing
            }
            .flatMapLatest { state ->
                getMangaDexUseCase(state.mangaDexIds)
            }
            .onEach { result ->
                mutableUiState.update { state ->
                    when(result) {
                        DataResult.Loading -> {
                            state.copy(
                                isLoading = true,
                                isRefreshing = false,
                                errorMessage = null
                            )
                        }
                        is DataResult.Success -> {
                            state.copy(
                                mangaList = result.data,
                                isLoading = false
                            )
                        }
                        is DataResult.Error -> {
                            state.copy(
                                errorMessage = result.message,
                                isLoading = false
                            )
                        }
                    }
                }
            }.launchIn(viewModelScope)
    }
}