package com.example.shikiflow.presentation.viewmodel.comment.section

import androidx.lifecycle.viewModelScope
import com.example.shikiflow.domain.usecase.GetCommentsUseCase
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
class CommentSectionViewModel @Inject constructor(
    private val getCommentsUseCase: GetCommentsUseCase
): UiStateViewModel<CommentSectionUiState>() {

    override val initialState: CommentSectionUiState = CommentSectionUiState()

    fun setTopicId(topicId: Int) {
        mutableUiState.update { state ->
            state.copy(topicId = topicId)
        }
    }

    fun onRefresh() {
        mutableUiState.update { state ->
            state.copy(isRefreshing = true)
        }
    }

    init {
        mutableUiState
            .filter { state ->
                state.topicId != null
            }
            .distinctUntilChanged { old, new ->
                old.topicId == new.topicId && old.commentsCount == new.commentsCount &&
                    !new.isRefreshing
            }
            .flatMapLatest { state ->
                getCommentsUseCase(state.topicId!!, state.commentsCount)
            }
            .onEach { result ->
                mutableUiState.update { state ->
                    when (result) {
                        is DataResult.Success -> {
                            state.copy(
                                comments = result.data,
                                isLoading = false,
                                errorMessage = null
                            )
                        }
                        is DataResult.Error -> {
                            state.copy(
                                isLoading = false,
                                errorMessage = result.message
                            )
                        }
                        else -> {
                            state.copy(
                                isLoading = true,
                                isRefreshing = false,
                                errorMessage = null
                            )
                        }
                    }
                }
            }.launchIn(viewModelScope)
    }
}