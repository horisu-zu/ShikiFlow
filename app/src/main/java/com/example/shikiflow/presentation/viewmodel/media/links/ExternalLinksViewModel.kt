package com.example.shikiflow.presentation.viewmodel.media.links

import androidx.lifecycle.viewModelScope
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.usecase.GetExternalLinksUseCase
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
class ExternalLinksViewModel @Inject constructor(
    private val getExternalLinksUseCase: GetExternalLinksUseCase
) : UiStateViewModel<ExternalLinksUiState>() {

    override val initialState: ExternalLinksUiState = ExternalLinksUiState()

    fun setMedia(mediaId: Int, mediaType: MediaType) {
        mutableUiState.update { state ->
            state.copy(
                mediaId = mediaId,
                mediaType = mediaType
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
                state.mediaId != null && state.mediaType != null
            }
            .distinctUntilChanged { old, new ->
                old.mediaId == new.mediaId && !new.isRefreshing
            }
            .flatMapLatest { state ->
                getExternalLinksUseCase(state.mediaId!!, state.mediaType!!)
            }
            .onEach { result ->
                mutableUiState.update { state ->
                    when (result) {
                        DataResult.Loading -> {
                            state.copy(
                                isLoading = true,
                                isRefreshing = false,
                                errorMessage = null
                            )
                        }

                        is DataResult.Success -> {
                            state.copy(
                                links = result.data,
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