package com.example.shikiflow.presentation.viewmodel.comment

import androidx.lifecycle.viewModelScope
import com.example.shikiflow.domain.model.comment.ALComment
import com.example.shikiflow.domain.model.comment.ALComment.Companion.findComment
import com.example.shikiflow.domain.model.comment.ALComment.Companion.updateComment
import com.example.shikiflow.domain.repository.CommentRepository
import com.example.shikiflow.presentation.PagedUiStateViewModel
import com.example.shikiflow.utils.result.DataResult
import com.example.shikiflow.utils.result.PagedResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CommentViewModel @Inject constructor(
    private val commentsRepository: CommentRepository
): PagedUiStateViewModel<CommentsUiState>() {

    override val initialState = CommentsUiState()

    init {
        mutableUiState
            .filter { state ->
                state.hasNextPage && state.topicId != null
            }
            .distinctUntilChanged { old, new ->
                old.page == new.page && !new.isRefreshing
            }
            .flatMapLatest { state ->
                commentsRepository.getThreadComments(state.topicId!!, state.page)
            }
            .onEach { result ->
                mutableUiState.update { state ->
                    if (result is PagedResult.Success) {
                        if (state.page == 1) state.comments.clear()
                        state.comments.addAll(result.list)

                        state.copy(
                            isLoading = false,
                            hasNextPage = result.hasNextPage
                        )
                    } else {
                        result.toUiState().copy(
                            hasNextPage = false,
                            isRefreshing = false
                        )
                    }
                }
            }.launchIn(viewModelScope)
    }

    fun setTopicId(topicId: Int) {
        mutableUiState.update { state ->
            state.copy(topicId = topicId)
        }
    }

    fun selectComment(commentId: Int) {
        mutableUiState.update { state ->
            state.copy(
                navState = state.navState + commentId
            )
        }
    }

    fun removeCommentFromStack() {
        mutableUiState.update { state ->
            state.copy(
                navState = state.navState.dropLast(1)
            )
        }
    }

    fun toggleLike(commentId: Int) {
        viewModelScope.launch {
            commentsRepository.toggleCommentLike(commentId).let { result ->
                if (result is DataResult.Success) {
                    val responseComment = result.data as ALComment

                    mutableUiState.update { state ->
                        val index = state.comments.indexOfFirst { comment ->
                            (comment as ALComment).findComment(responseComment.id) != null
                        }

                        if (index != -1) {
                            val root = state.comments[index] as ALComment

                            state.comments[index] = root.updateComment(commentId) { comment ->
                                comment.copy(
                                    likesCount = responseComment.likesCount,
                                    isLiked = responseComment.isLiked
                                )
                            }
                        }

                        state
                    }
                }
            }
        }
    }

    fun onRefresh() {
        mutableUiState.update { state ->
            state.copy(
                page = 1,
                isRefreshing = true,
                hasNextPage = true
            )
        }
    }

    fun onRetry() {
        mutableUiState.update { state ->
            state.copy(
                isRefreshing = true,
                hasNextPage = true
            )
        }
    }
}