package com.example.shikiflow.presentation.viewmodel.comment

import androidx.lifecycle.viewModelScope
import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.model.comment.ALComment
import com.example.shikiflow.domain.model.comment.ALComment.Companion.findComment
import com.example.shikiflow.domain.model.comment.ALComment.Companion.updateComment
import com.example.shikiflow.domain.model.thread.LikeableType
import com.example.shikiflow.domain.repository.CommentRepository
import com.example.shikiflow.domain.repository.SettingsRepository
import com.example.shikiflow.presentation.PagedUiStateViewModel
import com.example.shikiflow.utils.result.DataResult
import com.example.shikiflow.utils.result.PagedResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CommentViewModel @Inject constructor(
    private val commentsRepository: CommentRepository,
    private val settingsRepository: SettingsRepository
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
                        state.comments.addAll(result.list)

                        state.copy(
                            isLoading = if (state.thread != null && state.authType == AuthType.ANILIST) false
                                else if (state.authType == AuthType.SHIKIMORI) false
                                else state.isLoading,
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

        mutableUiState
            .filter { state ->
                state.authType == AuthType.ANILIST && state.topicId != null
            }
            .distinctUntilChangedBy { state -> state.topicId }
            .flatMapLatest { state ->
                commentsRepository.getThread(state.topicId!!)
            }.onEach { result ->
                mutableUiState
                    .update { state ->
                        when (result) {
                            is DataResult.Success -> {
                                state.copy(
                                    isLoading = if (state.comments.isNotEmpty()) false
                                        else state.isLoading,
                                    thread = result.data
                                )
                            }
                            is DataResult.Error -> {
                                state.copy(
                                    isLoading = false,
                                    errorMessage = result.message
                                )
                            }
                            else -> state
                        }
                    }
            }.launchIn(viewModelScope)

        settingsRepository.authTypeFlow
            .filterNotNull()
            .distinctUntilChanged()
            .onEach { authType ->
                mutableUiState.update { state ->
                    state.copy(
                        authType = authType
                    )
                }
            }.launchIn(viewModelScope)

        settingsRepository.userFlow
            .filterNotNull()
            .map { it.id }
            .distinctUntilChanged()
            .onEach { currentUserId ->
                mutableUiState.update { state ->
                    state.copy(
                        currentUserId = currentUserId
                    )
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

    fun toggleCommentLike(commentId: Int) {
        viewModelScope.launch {
            commentsRepository.toggleLike(commentId, LikeableType.COMMENT).let { result ->
                if (result is DataResult.Success) {
                    val response = result.data

                    mutableUiState.update { state ->
                        val index = state.comments.indexOfFirst { comment ->
                            (comment as ALComment).findComment(commentId) != null
                        }

                        if (index != -1) {
                            val root = state.comments[index] as ALComment

                            state.comments[index] = root.updateComment(commentId) { comment ->
                                comment.copy(
                                    likesCount = response.likeCount,
                                    isLiked = response.isLiked
                                )
                            }
                        }

                        state
                    }
                }
            }
        }
    }

    fun toggleThreadLike(threadId: Int) {
        viewModelScope.launch {
            commentsRepository.toggleLike(threadId, LikeableType.THREAD).let { result ->
                if (result is DataResult.Success) {
                    val response = result.data

                    mutableUiState.update { state ->
                        state.copy(
                            thread = state.thread?.copy(
                                isLiked = response.isLiked,
                                likeCount = response.likeCount
                            )
                        )
                    }
                }
            }
        }
    }

    fun onRefresh() {
        mutableUiState.update { state ->
            state.comments.clear()

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