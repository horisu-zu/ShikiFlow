package com.example.shikiflow.presentation.viewmodel.comment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.shikiflow.domain.repository.CommentRepository
import com.example.shikiflow.domain.usecase.GetCommentTopicUseCase
import com.example.shikiflow.utils.DataResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CommentViewModel @Inject constructor(
    private val getCommentTopicUseCase: GetCommentTopicUseCase,
    private val commentsRepository: CommentRepository
): ViewModel() {

    private var _uiState = MutableStateFlow(CommentsUiState())
    val uiState = _uiState.asStateFlow()

    val comments = _uiState
        .filter { state ->
            state.topicId != null
        }
        .distinctUntilChangedBy { state ->
            state.topicId
        }
        .flatMapLatest { state ->
            commentsRepository.getPaginatedComments(state.topicId!!)
        }.cachedIn(viewModelScope)

    init {
        _uiState
            .filter { state ->
                state.commentId != null
            }
            .distinctUntilChanged { old, new ->
                old.commentId == new.commentId && new.repliesMap[new.commentId]?.isRefreshing == false
            }
            .filter { state ->
                state.repliesMap[state.commentId]?.commentsMap?.isNotEmpty() != true
            }
            .flatMapLatest { state ->
                getCommentTopicUseCase(state.commentId!!)
                    .map { result -> state.commentId to result }
            }
            .onEach { (commentId, result) ->
                _uiState.update { state ->
                    val currentState = state.repliesMap[commentId] ?: RepliesUiState()

                    val newState = when (result) {
                        is DataResult.Loading -> currentState.copy(
                            isLoading = true,
                            errorMessage = null
                        )
                        is DataResult.Success -> currentState.copy(
                            isLoading = false,
                            commentsMap = result.data
                        )
                        is DataResult.Error -> currentState.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }

                    state.copy(
                        repliesMap = state.repliesMap + (commentId to newState)
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun setTopicId(topicId: Int) {
        _uiState.update { state ->
            state.copy(topicId = topicId)
        }
    }

    fun setCommentId(commentId: Int) {
        _uiState.update { state ->
            state.copy(commentId = commentId)
        }
    }

    fun onRefresh() {
        _uiState.update { state ->
            val commentId = state.commentId ?: return@update state

            state.copy(
                repliesMap = state.repliesMap.toMutableMap().apply {
                    this[commentId] = this[commentId]?.copy(
                        isRefreshing = true
                    ) ?: return@update state
                }
            )
        }
    }
}