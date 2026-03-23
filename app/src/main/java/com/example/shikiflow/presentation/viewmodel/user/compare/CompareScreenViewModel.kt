package com.example.shikiflow.presentation.viewmodel.user.compare

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.usecase.GroupUserRatesUseCase
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
class CompareScreenViewModel @Inject constructor(
    private val groupUserRatesUseCase: GroupUserRatesUseCase
): ViewModel() {

    private val _uiState = MutableStateFlow(CompareScreenUiState())
    val uiState = _uiState.asStateFlow()

    fun setData(currentUserId: String, targetUserId: String, mediaType: MediaType) {
        _uiState.update { state ->
            state.copy(
                currentUserId = currentUserId,
                targetUserId = targetUserId,
                mediaType = mediaType
            )
        }
    }

    fun onRefresh(mediaType: MediaType) {
        updateMediaState(mediaType) { state ->
            state.copy(isRefreshing = true)
        }
    }

    private fun updateMediaState(type: MediaType, transform: (CompareMediaUiState) -> CompareMediaUiState) {
        _uiState.update { state ->
            state.copy(
                mediaUiState = state.mediaUiState.toMutableMap().apply {
                    this[type] = transform(this[type] ?: CompareMediaUiState())
                }
            )
        }
    }

    init {
        MediaType.entries.forEach { mediaType ->
            _uiState
                .filter { state ->
                    state.currentUserId != null &&
                        state.targetUserId != null &&
                        state.mediaType == mediaType
                }
                .distinctUntilChanged { old, new ->
                    old.targetUserId == new.targetUserId &&
                        new.mediaUiState[new.mediaType!!]?.isRefreshing == false
                }
                .flatMapLatest { state ->
                    groupUserRatesUseCase(
                        currentUserId = state.currentUserId!!,
                        targetUserId = state.targetUserId!!,
                        mediaType = state.mediaType!!
                    )
                }
                .onEach { result ->
                    updateMediaState(mediaType) { mediaState ->
                        when (result) {
                            is DataResult.Loading -> {
                                mediaState.copy(
                                    isLoading = true,
                                    errorMessage = null
                                )
                            }
                            is DataResult.Success -> {
                                mediaState.copy(
                                    userRates = result.data,
                                    isLoading = false,
                                    isRefreshing = false
                                )
                            }
                            is DataResult.Error -> {
                                mediaState.copy(
                                    errorMessage = result.message,
                                    isLoading = false,
                                    isRefreshing = false
                                )
                            }
                        }
                    }
                }.launchIn(viewModelScope)
        }
    }
}