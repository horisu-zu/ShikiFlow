package com.example.shikiflow.presentation.viewmodel.anime.watch.translations

import androidx.lifecycle.viewModelScope
import com.example.shikiflow.domain.usecase.GetAnimeTranslationsUseCase
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
class AnimeTranslationsViewModel @Inject constructor(
    private val getAnimeTranslationsUseCase: GetAnimeTranslationsUseCase
): UiStateViewModel<AnimeTranslationsUiState>() {

    override val initialState: AnimeTranslationsUiState = AnimeTranslationsUiState()

    fun setId(animeId: Int) {
        mutableUiState.update { state ->
            state.copy(
                animeId = animeId
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
                state.animeId != null
            }
            .distinctUntilChanged { old, new ->
                old.animeId == new.animeId && !new.isRefreshing
            }
            .flatMapLatest { state ->
                getAnimeTranslationsUseCase(state.animeId!!)
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
                                translations = result.data,
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